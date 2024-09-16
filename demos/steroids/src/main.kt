import gameplay.registerProcessSystem
import korlibs.image.color.Colors
import korlibs.inject.Injector
import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.korge.view.Views
import korlibs.math.geom.SizeInt
import scenes.*
import kotlin.reflect.KClass


suspend fun main() = Korge(Korge.Config(module = SteroidsGameModule))

object SteroidsGameModule : Module() {
	override val title = "Steroids"
	override val size = SizeInt(640,480)
	//override val windowSize = SizeInt(800, 600)
	override val windowSize = SizeInt(640, 480)
	override val targetFps = 24.0

	override val bgcolor = Colors.BLACK
	override val mainScene: KClass<out Scene> = GameScene::class

	override suspend fun init(injector: Injector): Unit = injector.run {
		//mapInstance(GameState())
		get<Views>().registerProcessSystem()

		mapPrototype { LoadingScene(/*get()*/) }
		mapPrototype { TitleScene(/*get()*/) }
		mapPrototype { GameScene(/*get()*/) }

	}
}
