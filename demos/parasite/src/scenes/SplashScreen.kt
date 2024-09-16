package scenes

import korlibs.audio.sound.readMusic
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.scene.Scene
import korlibs.korge.scene.delay
import korlibs.korge.tween.tween
import korlibs.korge.view.Image
import korlibs.korge.view.SContainer
import korlibs.korge.view.image
import korlibs.time.seconds
import util.LoadingProxyScene

class SplashScreen : Scene() {
    
    private lateinit var bg: Image
    private lateinit var bgMusic: NativeSoundChannel
    
    override suspend fun SContainer.sceneInit() {
        bgMusic = resourcesVfs["sounds/intro_loop.wav"].readMusic().play()
        bg = image(resourcesVfs["graphics/splash_scene/intro_bg.png"].readBitmap()) {
            smoothing = false
            alpha = 0.0
        }
    }
    
    override suspend fun sceneAfterInit() {
        bg.tween(bg::alpha[1.0], time = 1.seconds)
        delay(2.seconds)
        bg.tween(bg::alpha[0.0], time = .5.seconds)
        sceneContainer.changeTo(LoadingProxyScene::class::class,
                LoadingProxyScene.NextScreen(MainScene::class),
                time = .5.seconds)
    }
    
    override suspend fun sceneBeforeLeaving() {
        sceneContainer.tween(bgMusic::volume[0.0], time = .4.seconds)
        bgMusic.stop()
    }
    
}