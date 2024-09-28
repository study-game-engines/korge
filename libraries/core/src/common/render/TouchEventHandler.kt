package korlibs.render

import korlibs.concurrent.lock.NonRecursiveLock
import korlibs.datastructure.Pool
import korlibs.event.TouchEvent

class TouchEventHandler {
    @PublishedApi
    internal val lock = NonRecursiveLock()
    @PublishedApi
    internal val touchesEventPool = Pool { TouchEvent() }
    @PublishedApi
    internal var lastTouchEvent: TouchEvent = TouchEvent()

    inline fun handleEvent(gameWindow: GameWindow, kind: TouchEvent.Type, emitter: (TouchEvent) -> Unit) {
        val currentTouchEvent = lock {
            val currentTouchEvent = touchesEventPool.alloc()
            currentTouchEvent.copyFrom(lastTouchEvent)
            currentTouchEvent.startFrame(kind)
            emitter(currentTouchEvent)
            currentTouchEvent.endFrame()

            lastTouchEvent.copyFrom(currentTouchEvent)
            currentTouchEvent
        }

        gameWindow.queue {
            try {
                gameWindow.dispatch(currentTouchEvent)
            } finally {
                lock { touchesEventPool.free(currentTouchEvent) }
            }
        }
    }
}
