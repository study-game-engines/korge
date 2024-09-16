package j4k.candycrush.compontens

import j4k.candycrush.GameOverEvent
import j4k.candycrush.ResetGameEvent
import j4k.candycrush.ShuffleGameEvent
import j4k.candycrush.lib.EventBus
import j4k.candycrush.lib.Resolution
import j4k.candycrush.lib.Resources
import j4k.candycrush.lib.centered
import j4k.candycrush.lib.textCentered
import korlibs.image.bitmap.NinePatchBitmap32
import korlibs.inject.Injector
import korlibs.korge.input.onClick
import korlibs.korge.view.Container
import korlibs.korge.view.image
import korlibs.korge.view.ninePatch
import korlibs.korge.view.position
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle

class GameOverComponent(bus: EventBus, res: Resources, resolution: Resolution) : Container() {

    init {
        bus.register<GameOverEvent> { visible = true }
        visible = false
        val center = resolution.center()

        val messageBox = MessageBox(450, 400, center, res.messageBox)
        addChild(messageBox)
        val shadowCorrection = 10
        val textPos = Point(center.x - shadowCorrection, messageBox.pos.y)
        textCentered(text = "Game Over", textSize = 64.0, font = res.fontCandy, center = textPos.top(80))

        addChild(CandyButton("Restart", ResetGameEvent, bus, res, textPos.top(200), this::hide))
        addChild(CandyButton("Next", ShuffleGameEvent, bus, res, textPos.top(310), this::hide))
    }

    companion object {
        suspend operator fun invoke(injector: Injector): GameOverComponent {
            injector.run {
                return GameOverComponent(get(), get(), get())
            }
        }
    }

    private fun hide() {
        visible = false
    }

    class MessageBox(width: Number, height: Number, center: Point, texture: NinePatchBitmap32) : Container() {
        init {
            val pos = Point(center.x - width.toDouble() / 2, center.y - height.toDouble() / 2)
            position(pos)
            ninePatch(ninePatch = texture, width = width.toDouble(), height = height.toDouble()) {}
        }
    }

    class CandyButton(text: String, event: Any, val bus: EventBus, res: Resources, point: Point, run: () -> Unit) :
        Container() {

        init {
            val texture = res.imageButton
            val point1 = texture.centered(point)
            position(point1)
            image(texture = texture) {
                val imageBounds = getLocalBounds(Rectangle())
                val center = Point(imageBounds.width / 2, imageBounds.height / 2)
                textCentered(text = text, textSize = 55.0, font = res.fontSmall, center = center)
                onClick {
                    run.invoke()
                    bus.send(event)
                }
            }
        }
    }


}
