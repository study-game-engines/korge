package game.scene

import korlibs.image.color.Colors
import korlibs.io.async.launchImmediately
import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.view.SContainer
import korlibs.korge.view.position
import korlibs.korge.view.solidRect
import korlibs.korge.view.text

class SplashScene : Scene() {
    override suspend fun SContainer.sceneInit() {
        text("Dungeon")
        solidRect(100.0, 100.0, Colors.RED).position(100, 100).onClick {
            launchImmediately { sceneContainer.changeTo(SplashScene::class) }
        }
    }
}