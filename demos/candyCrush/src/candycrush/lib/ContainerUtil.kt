package j4k.candycrush.lib

import korlibs.image.font.BitmapFont
import korlibs.korge.view.Container
import korlibs.korge.view.position
import korlibs.korge.view.text
import korlibs.math.geom.Point

fun Container.textCentered(text: String, textSize: Double = 16.0, font: BitmapFont, center: Point) {
    this.text(text, font = font, textSize = textSize) {
        val textBounds = this.getLocalBounds()
        position(center.x - (textBounds.width / 2), center.y - (textBounds.height / 2))
    }
}
