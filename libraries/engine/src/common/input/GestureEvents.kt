package korlibs.korge.input

import korlibs.datastructure.Extra
import korlibs.event.GestureEvent
import korlibs.io.async.Signal
import korlibs.io.async.launchImmediately
import korlibs.korge.view.BaseView
import korlibs.korge.view.View
import korlibs.korge.view.Views
import kotlin.reflect.KProperty1

class GestureEvents(val view: BaseView) {

    val lastEvent = GestureEvent()
    val magnify = Signal<GestureEvents>() // Trackpad pinch zooming. Only implemented on MacOS for now
    val rotate = Signal<GestureEvents>() // Trackpad pinch rotation. Only implemented on MacOS for now
    val swipe = Signal<GestureEvents>()
    val smartZoom = Signal<GestureEvents>()
    val id: Int get() = lastEvent.id
    val amount: Float get() = lastEvent.amount

    lateinit var views: Views
        private set

    init {
        view.onEvents(*GestureEvent.Type.ALL) { event ->
            this.views = event.target as Views
            lastEvent.copyFrom(event)
            when (event.type) {
                GestureEvent.Type.MAGNIFY -> this.magnify(this)
                GestureEvent.Type.ROTATE -> this.magnify(this)
                GestureEvent.Type.SWIPE -> this.swipe(this)
                GestureEvent.Type.SMART_MAGNIFY -> this.smartZoom(this)
            }
        }
    }

}

val View.gestures by Extra.PropertyThis { GestureEvents(this) }

inline fun <T> View.gestures(callback: GestureEvents.() -> T): T = gestures.run(callback)

/** Trackpad pinch zooming. Only implemented on MacOS for now */
inline fun <T : View?> T.onMagnify(noinline handler: @EventsDslMarker suspend (GestureEvents) -> Unit) =
    doGestureEvent(GestureEvents::magnify, handler)

inline fun <T : View?> T.onSwipe(noinline handler: @EventsDslMarker suspend (GestureEvents) -> Unit) =
    doGestureEvent(GestureEvents::swipe, handler)

/** Trackpad pinch rotation. Only implemented on MacOS for now */
inline fun <T : View?> T.onRotate(noinline handler: @EventsDslMarker suspend (GestureEvents) -> Unit) =
    doGestureEvent(GestureEvents::rotate, handler)

inline fun <T : View?> T.onSmartZoom(noinline handler: @EventsDslMarker suspend (GestureEvents) -> Unit) =
    doGestureEvent(GestureEvents::smartZoom, handler)

@PublishedApi
internal inline fun <T : View?> T?.doGestureEvent(prop: KProperty1<GestureEvents, Signal<GestureEvents>>, noinline handler: suspend (GestureEvents) -> Unit): T? {
    this?.gestures?.let { gestures ->
        prop.get(gestures).add { launchImmediately(gestures.views.coroutineContext) { handler(it) } }
    }
    return this
}
