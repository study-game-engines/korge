package de.tfr.game.ui

import de.tfr.game.Controller
import de.tfr.game.lib.actor.Point
import de.tfr.game.lib.actor.Point2D
import de.tfr.game.renderer.ButtonTiles
import de.tfr.game.toControl
import korlibs.event.KeyEvent
import korlibs.korge.input.onDown
import korlibs.korge.input.onUp
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.View
import korlibs.korge.view.anchor
import korlibs.korge.view.image
import korlibs.korge.view.position

class Button(val control: Controller.Control,
        center: Point2D,
        private val style: ButtonTiles.ButtonImage,
        val view: View) : Point by center {

    var clickListener: (() -> Any)? = null

    private lateinit var image: Image

    fun create(container: Container): Button {
        val posX = x
        val posY = y
        image = container.image(style.normal) {
            position(posX, posY)
            anchor(.5, .5)
        }
        image.onDown { }
        image.onUp { setUp() }
        image.onDown { setDown() }
        image.keys {
            down {
                ifControlPressed(it) { setDown() }
            }
            up {
                ifControlPressed(it) { setUp() }
            }
        }
        return this
    }

    private fun setDown() {
        image.bitmap = style.pressed
        clickListener?.invoke()
    }

    private fun setUp() {
        image.bitmap = style.normal
    }

    private fun ifControlPressed(e: KeyEvent, action: () -> Any) {
        if (e.key.toControl() == control) {
            action.invoke()
        }
    }

}