package korlibs.render.awt

import korlibs.event.gamepad.LinuxJoyEventAdapter
import korlibs.event.gamepad.XInputGamepadEventAdapter
import korlibs.platform.Platform
import korlibs.render.GameWindow
import korlibs.time.Stopwatch
import korlibs.time.seconds

internal object DesktopGamepadUpdater {
    private var exceptionStopwatch: Stopwatch? = null

    fun updateGamepads(window: GameWindow) {
        if (exceptionStopwatch != null && exceptionStopwatch!!.elapsed < 1.seconds) {
            return
        }
        if (exceptionStopwatch == null) {
            exceptionStopwatch = Stopwatch().start()
        }
        //println("exceptionStopwatch.elapsed=${exceptionStopwatch?.elapsed}")
        try {
            when {
                Platform.isWindows -> xinputEventAdapter.updateGamepads(window.gamepadEmitter)
                Platform.isLinux -> linuxJoyEventAdapter.updateGamepads(window.gamepadEmitter)
                else -> Unit //println("undetected OS: ${OS.rawName}")
            }
        } catch (e: Throwable) {
            exceptionStopwatch?.restart()
            //println("exceptionStopwatch.restart()")
            e.printStackTrace()
        }
    }

    private val xinputEventAdapter by lazy { XInputGamepadEventAdapter() }
    private val linuxJoyEventAdapter by lazy { LinuxJoyEventAdapter() }
}
