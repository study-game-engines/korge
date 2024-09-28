package korlibs.render

import korlibs.io.async.launchImmediately
import korlibs.js.Deno
import korlibs.math.geom.Size
import korlibs.platform.jsGlobalThis
import korlibs.sdl.SDLGameWindow

class DenoJsGameWindow(size: Size = Size(640, 480), config: GameWindowCreationConfig = GameWindowCreationConfig.DEFAULT) : SDLGameWindow(size, config) {

    override suspend fun loop(entry: suspend GameWindow.() -> Unit) {
        launchImmediately(getCoroutineDispatcherWithCurrentContext()) {
            entry()
        }
        jsGlobalThis.setInterval({
            updateSDLEvents()
            frame()
            afterFrame()
        }, 16)
    }

    override fun close(exitCode: Int) {
        super.close(exitCode)
        Deno.exit(exitCode)
    }

}
