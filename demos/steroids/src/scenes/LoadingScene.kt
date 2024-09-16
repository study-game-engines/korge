package scenes

import korlibs.concurrent.thread.NativeThread.Companion.sleep
import korlibs.image.bitmap.BmpSlice
import korlibs.image.bitmap.slice
import korlibs.image.color.Colors
import korlibs.image.format.readBitmap
import korlibs.io.async.async
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.scene.Scene
import korlibs.korge.tween.tween
import korlibs.korge.view.Container
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.centerXBetween
import korlibs.korge.view.align.centerYBetween
import korlibs.korge.view.image
import korlibs.math.interpolation.EASE_IN_OUT
import korlibs.math.interpolation.Easing
import korlibs.time.seconds
import resources.Resources

class LoadingScene() : Scene() {

    override suspend fun SContainer.sceneInit() {
        views.clearColor = Colors.BLACK
    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        val splash = async { sceneView.splash() }

        sleep(1.0.seconds)
        Resources(views).loadAll()

        splash.await()
        sceneContainer.changeTo(TitleScene::class)
    }

    suspend fun Container.splash() {
        val map = resourcesVfs["korge.png"].readBitmap().slice()
        val anim = async {
            logo(map)
        }
        sleep(1.seconds)
        anim.await()
    }

    suspend fun Container.logo(graph: BmpSlice) {
        val image = image(graph) {
            alpha = 0.0
            centerXBetween(0,640)
            centerYBetween(0,480)
        }

        image.tween(image::alpha[1], time = 1.seconds, easing = Easing.EASE_IN_OUT)
        sleep(1.seconds)
        image.tween(image::alpha[0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
    }
}