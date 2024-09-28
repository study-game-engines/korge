package korlibs.korge

import korlibs.korge.view.Views

internal actual fun completeViews(views: Views) {
    // Already performed on Korge start
    //HtmlSimpleSound.unlock // Tries to unlock audio as soon as possible
}

internal actual fun beforeStartingKorge(config: Korge) {
    // no op
}
