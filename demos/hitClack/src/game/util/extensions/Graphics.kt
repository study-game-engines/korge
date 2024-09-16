package de.tfr.game.util.extensions

import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.font.Font
import korlibs.korge.view.Container
import korlibs.korge.view.Graphics
import korlibs.korge.view.Text
import korlibs.korge.view.position
import korlibs.math.geom.vector.VectorPath

fun Graphics.circle(x: Number, y: Number, radius: Number) {
    this.path { circle(x, y, radius) }
}

fun Graphics.path(path: (VectorPath) -> Unit = {}) {
    this.shape(VectorPath().apply(path))
}


fun Graphics.drawFill(color: RGBA, renderAction: (Graphics) -> Any) {
    this.beginFill(color)
    renderAction.invoke(this)
    this.endFill()
}

fun Container.text(
  text: String,
  x: Double,
  y: Double,
  font: Font,
  textSize: Double,
  color: RGBA = Colors.WHITE
): Text {

    return text(text, textSize, font = font, color = color) {
        position(x, y)
    }
}

fun Graphics.triangle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) {
    this.moveTo(x1, y1)
    this.lineTo(x2, y2)
    this.lineTo(x3, y3)
    this.lineTo(x1, y1)
}