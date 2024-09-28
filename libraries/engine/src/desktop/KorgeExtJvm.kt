package korlibs.korge

import korlibs.korge.view.Views
import kotlinx.coroutines.debug.DebugProbes
import java.util.*

interface ViewsCompleter {
    fun completeViews(views: Views)
}

internal actual fun completeViews(views: Views) {
    for (completer in ServiceLoader.load(ViewsCompleter::class.java).toList()) {
        completer.completeViews(views)
    }
}

internal actual fun beforeStartingKorge(config: Korge) {
    if (config.realDebugCoroutines) {
        DebugProbes.enableCreationStackTraces = true
        DebugProbes.install()
    }
}
