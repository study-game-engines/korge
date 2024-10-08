package de.tfr.game.renderer

import de.tfr.game.Display
import de.tfr.game.ui.GRAY_DARK
import de.tfr.game.ui.GREEN_LIGHT
import de.tfr.game.ui.GREEN_LIGHT2
import de.tfr.game.util.extensions.text
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.font.BitmapFont
import korlibs.image.font.readBitmapFont
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.position
import korlibs.korge.view.solidRect
import korlibs.logger.Logger


class DisplayRenderer(private val display: Display) {

    private val log = Logger<DisplayRenderer>()
    lateinit var font: BitmapFont
    var text: Text? = null

    suspend fun init(container: Container) = apply {
        font = resourcesVfs["fonts/segment7.fnt"].readBitmapFont()

        container.solidRect(display.width, display.height, GREEN_LIGHT) {
            position(display.borderLeftX(), display.y + 500)
        }

        container.timeText("00:22", GREEN_LIGHT2)

        text = container.timeText(display.getText(), GRAY_DARK)
    }

    private fun Container.timeText(text: String, color: RGBA = Colors.WHITE): Text {
        return this.text(text, display.borderLeftX() + 5, display.y + 506, font, 92.0, color)
    }

    fun render() {
        val newTime = display.getText()
        log.trace { newTime }
        text?.text = newTime
    }

}
