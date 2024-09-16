package j4k.candycrush.compontens

import korlibs.image.bitmap.Bitmap
import korlibs.korge.input.onClick
import korlibs.korge.input.onOut
import korlibs.korge.input.onOver
import korlibs.korge.view.Container
import korlibs.korge.view.Image

class CheckBox(bitmapOn: Bitmap, bitmapOff: Bitmap, bitmapHover: Bitmap? = null, initial: Boolean = true, var action: suspend (Boolean) -> Unit = {}) : Container() {

    private var checked: Boolean = initial

    private val on = Image(bitmapOn)
    private val off = Image(bitmapOff)
    private val hover = bitmapHover?.let(::Image)

    init {
        addChild(off)
        addChild(on)
        if (hover != null) {
            addChild(hover)
            onOver {
                hover.visible = true
            }
            onOut {
                hover.visible = false
            }
        }
        updateState()
        onClick {
            toggle()
        }
    }

    suspend fun toggle() {
        checked = !checked
        updateState()
        action.invoke(checked)
    }

    private fun updateState() {
        on.visible = checked
        off.visible = !checked
    }
}
