package j4k.candycrush.compontens

import korlibs.image.bitmap.Bitmap
import korlibs.korge.input.onClick
import korlibs.korge.input.onDown
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.input.onUp
import korlibs.korge.view.Container
import korlibs.korge.view.Image

class Button(imageNormal: Bitmap, imageHover: Bitmap, imageClicked: Bitmap, var action: suspend () -> Unit = {}) : Container() {

    private val normal = Image(imageNormal)
    private val hover = Image(imageHover)
    private val clicked = Image(imageClicked)

    init {
        addChild(normal)
        addChild(hover)
        addChild(clicked)
        hover.visible = false
        clicked.visible = false

        onClick {
            action.invoke()
        }
        onDown {
            clicked.visible = true
        }
        onUp {
            clicked.visible = false
        }
        onOver {
            hover.visible = true
        }
        onOut {
            hover.visible = false
            clicked.visible = false
        }
    }
}
