import editor.*
import korlibs.image.color.Colors
import korlibs.inject.injector
import korlibs.io.file.std.MemoryVfsMix
import korlibs.korge.*
import korlibs.korge.Korge
import korlibs.korge.annotations.*
import korlibs.korge.scene.*

@OptIn(KorgeExperimental::class)
suspend fun main() = Korge(
    windowSize = Korge.DEFAULT_WINDOW_SIZE,
    virtualSize = Korge.DEFAULT_WINDOW_SIZE,
	backgroundColor = Colors["#2b2b2b"],
	displayMode = KorgeDisplayMode.CENTER_NO_CLIP,
    debug = false,
    debugCoroutines = true
) {
    injector().mapPrototype { ParticleEditorScene() }
    val sceneContainer = sceneContainer()
    sceneContainer.changeTo(ParticleEditorScene::class, EditorFile(MemoryVfsMix("particle.pex" to "")))
}
