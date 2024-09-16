import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.inject.Injector
import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.SizeInt
import scenes.*
import util.LoadingProxyScene
import kotlin.reflect.KClass

suspend fun main() = Korge(Korge.Config(module = MainModule))

object MainModule : Module() {
    
    override val mainScene: KClass<out Scene>
        get() = SplashScreen::class
    override val title: String
        get() = "Parasite"
    override val windowSize: SizeInt
        get() = SizeInt(1280, 720)
    override val size: SizeInt
        get() = SizeInt(320, 180)
    override val scaleMode: ScaleMode
        get() = ScaleMode.COVER
    override val bgcolor: RGBA
        get() = Colors.BLACK
    
    override suspend fun Injector.configure() {
        mapPrototype { MainScene() }
        mapPrototype { GameScene() }
        mapPrototype { SplashScreen() }
        mapPrototype { LoadingProxyScene(get(), getOrNull()) }
    }
}