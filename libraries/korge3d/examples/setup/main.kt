package korlibs.korge3d

import korlibs.image.color.*
import korlibs.io.async.runBlockingNoJs
import korlibs.korge.*
import korlibs.korge.scene.sceneContainer
import korlibs.render.*

fun main() = runBlockingNoJs() {
    Korge(
        windowSize = Korge.DEFAULT_WINDOW_SIZE,
        virtualSize = Korge.DEFAULT_WINDOW_SIZE,
        backgroundColor = Colors["#3f3f3f"],
        displayMode = KorgeDisplayMode.CENTER_NO_CLIP,
        debug = false,
        debugCoroutines = true,
        quality = GameWindow.Quality.QUALITY
    ) {
        views.injector
            .mapPrototype { MainStage3d() }
            .mapPrototype { PhysicsScene() }
            .mapPrototype { CratesScene() }
            .mapPrototype { MonkeyScene() }
            .mapPrototype { SkinningScene() }
        sceneContainer().changeTo(MainStage3d::class)
    }
}