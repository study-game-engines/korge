package views

import korlibs.image.color.Colors
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.mouse
import korlibs.korge.scene.SceneContainer
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.anchor
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.time.seconds
import scenes.GameScene
import util.LoadingProxyScene
import scenes.MainScene

class GameOverPanel(val sceneContainer: SceneContainer) : Container() {
    
    private lateinit var bg: Image
    private lateinit var gameOverText: Image
    private lateinit var menuButton: Image
    private lateinit var restartButton: Image
    
    suspend fun loadPanel() {
        bg = image(resourcesVfs["graphics/game_over_scene/game_over_bg.png"].readBitmap()) {
            smoothing = false
            tint = Colors.DARKMAGENTA
        }
        
        gameOverText = image(resourcesVfs["graphics/game_over_scene/game_over_text.png"].readBitmap()) {
            anchor(.5, .5)
            scale = .5
            position(this@GameOverPanel.width / 2 - 20, 40)
            smoothing = false
        }
        
        menuButton = image(resourcesVfs["graphics/game_over_scene/menu_button.png"].readBitmap()) {
            tint = Colors.DARKMAGENTA
            position(25, this@GameOverPanel.height - 50)
            smoothing = false
            mouse {
                over {
                    tint = Colors.MAGENTA
                }
                out {
                    tint = Colors.DARKMAGENTA
                }
                onClick {
                    if (mouseEnabled) {
                        mouseEnabled = false
                        scale -= 0.05
                        sceneContainer.changeTo(
                                LoadingProxyScene::class,
                                LoadingProxyScene.NextScreen(MainScene::class),
                                time = .5.seconds
                        )
                    }
                }
            }
        }
        
        restartButton = image(resourcesVfs["graphics/game_over_scene/restart_button.png"].readBitmap()) {
            tint = Colors.DARKMAGENTA
            position(25, this@GameOverPanel.height - 100)
            smoothing = false
            mouse {
                over {
                    tint = Colors.MAGENTA
                }
                out {
                    tint = Colors.DARKMAGENTA
                }
                onClick {
                    if (mouseEnabled) {
                        mouseEnabled = false
                        scale -= 0.05
                        sceneContainer.changeTo<LoadingProxyScene>(
                                LoadingProxyScene.NextScreen(GameScene::class),
                                time = .5.seconds
                        )
                    }
                }
            }
        }
    }
}