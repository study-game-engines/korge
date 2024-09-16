package tfr.korge.jam.roguymaze.lib

import korlibs.image.bitmap.NinePatchBitmap32
import korlibs.image.font.BitmapFont
import korlibs.korge.view.Container
import korlibs.korge.view.NinePatch
import korlibs.korge.view.position
import korlibs.korge.view.text
import korlibs.math.geom.Point
import korlibs.math.geom.Size


fun Container.textCentered(text: String, textSize: Double = 16.0, font: BitmapFont, center: Point) {
    this.text(text, font = font, textSize = textSize) {
        val textBounds = this.getLocalBounds()
        position(center.x - (textBounds.width / 2), center.y - (textBounds.height / 2))
    }
}

fun Container.ninePatch(ninePatch: NinePatchBitmap32, size: Size, callback: @ViewsDslMarker (NinePatch.() -> Unit) = {}) {
    this.ninePatch(ninePatch = ninePatch, width = size.width, height = size.height, callback = callback)
}