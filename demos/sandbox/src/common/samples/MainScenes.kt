package samples

import korlibs.image.color.Colors
import korlibs.korge.input.onClickSuspend
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.*
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.align.centerXOn

class MainScenes : Scene() {
    override suspend fun SContainer.sceneMain() {
        injector.root
            .mapInstance(MyDependency("HELLO WORLD"))
            .mapPrototype { MyScene1(get()) }
            .mapPrototype { MyScene2(get()) }
        sceneContainer().changeTo(MyScene1::class)
    }


    class MyDependency(val value: String)

    companion object {
        const val MARGIN = 10
    }

    class MyScene1(private val myDependency: MyDependency) : Scene() {
        override suspend fun SContainer.sceneMain() {
            val mainText = text("MyScene1: ${myDependency.value}", 32.0) {
                smoothing = false
                position(MARGIN, MARGIN)
            }
            text("Click any square to switch to MyScene2") {
                alignTopToBottomOf(mainText, 10)
                positionX(MARGIN)
            }

            solidRect(100, 100, Colors.RED) {
                position(200, 200)
                alpha = 0.7
                onOver { alpha = 1.0 }
                onOut { alpha = 0.7 }
                onClickSuspend {
                    sceneContainer.changeTo(MyScene2::class)
                }
            }
            solidRect(100, 100, Colors.BLUE) {
                position(250, 250)
                alpha = 0.7
                onOver { alpha = 1.0 }
                onOut { alpha = 0.7 }
                onClickSuspend {
                    sceneContainer.changeTo(MyScene2::class)
                }
            }
        }
    }

    class MyScene2(private val myDependency: MyDependency) : Scene() {
        override suspend fun SContainer.sceneMain() {
            text("MyScene2: ${myDependency.value}", 32.0) {
                smoothing = false
                position(MARGIN, 10)
            }

            val blueSquare = solidRect(100, 100, Colors.BLUE) {
                position(200, 200)
                onClickSuspend {
                    sceneContainer.changeTo(MyScene1::class, MyDependency("From MyScene2"))
                }
            }

            text("Click the square to switch to MyScene1") {
                alignTopToBottomOf(blueSquare, 10)
                centerXOn(blueSquare)
            }
        }
    }
}
