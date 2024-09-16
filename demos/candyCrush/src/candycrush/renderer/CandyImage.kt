package j4k.candycrush.renderer

import j4k.candycrush.model.*
import korlibs.image.bitmap.BmpSlice
import korlibs.korge.view.BaseImage
import korlibs.korge.view.anchor
import korlibs.korge.view.position
import korlibs.korge.view.size
import korlibs.math.geom.Point

class CandyImage(
    tileSize: Double,
    position: Point,
    private val candy: BmpSlice,
    private val debugLetter: BmpSlice,
    val tile: Tile
) : BaseImage(candy) {
    init {
        anchor(0.5, 0.5)
        size(tileSize, tileSize)
        position(position)
    }

    fun debug() {
        bitmap = debugLetter
    }

    fun disableDebug() {
        bitmap = candy
    }

    override fun toString(): String {
        return "\n\n $tile: ($x,$y)"
    }
}
