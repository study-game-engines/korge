package de.tfr.game.util.extensions

import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.font.Font
import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.addTo

inline fun Container.text(
        text: String,
        textSize: Double = 16.0,
        font: Font,
        color: RGBA = Colors.WHITE,
        callback: Text.() -> Unit = {}
) = Text(
        text, textSize = textSize, font = font, color = color).addTo(this).apply(callback)