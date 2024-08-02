package korge.sandbox

import korlibs.io.async.runBlockingNoJs
import korlibs.korge.Korge
import korlibs.korge.KorgeDisplayMode

fun launcher() = runBlockingNoJs {
    Korge(
        windowSize = Korge.DEFAULT_WINDOW_SIZE,
        virtualSize = Korge.DEFAULT_WINDOW_SIZE,
        backgroundColor = DEFAULT_KORGE_BG_COLOR,
        displayMode = KorgeDisplayMode.CENTER_NO_CLIP,
        debug = false,
        forceRenderEveryFrame = true
    ) {
        demoSelector(
            Demo(::MainRenderImagesJvmNative),
            listOf(
                Demo(::MainRenderImagesJvmNative)
            )
        )
    }
}
