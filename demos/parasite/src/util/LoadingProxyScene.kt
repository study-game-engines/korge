package util

import korlibs.korge.scene.Scene
import korlibs.korge.time.delay
import korlibs.korge.tween.tween
import korlibs.korge.view.Image
import korlibs.korge.view.SContainer
import korlibs.korge.view.Text
import korlibs.korge.view.position
import korlibs.korge.view.text
import korlibs.time.seconds
import kotlin.reflect.KClass

class LoadingProxyScene(nextScreen: NextScreen, private val infoImage: Image?) : Scene() {
    
    private lateinit var loadingText: Text
    private val text: String = "Loading..."
    private val nextScreen: KClass<*> = nextScreen.nextScreenClass
    
    override suspend fun SContainer.sceneInit() {
        infoImage?.let { info ->
            info.anchor(.5, .5)
            info.position(views.virtualWidth / 2, views.virtualHeight / 2)
            info.scale = .8
            addChild(info)
        }
        
        loadingText = text(text) {
            textSize = 6.0
            position(-this.width, views.virtualHeight - 20)
            filtering = false
        }
    }
    
    override suspend fun SContainer.sceneMain() {
        loadingText.tween(loadingText::x[views.virtualWidth - loadingText.width - 20].easeOut(), time = 1.seconds)
        infoImage?.let { delay(3.seconds) }
        sceneContainer.changeTo(clazz = nextScreen as KClass<Scene>, time = .5.seconds)
    }
    
    override suspend fun sceneBeforeLeaving() {
        loadingText.tween(loadingText::x[views.virtualWidth].easeIn(), time = .5.seconds)
    }
    
    data class NextScreen(val nextScreenClass: KClass<*>)
}