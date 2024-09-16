package tfr.korge.jam.roguymaze.renderer

import korlibs.image.bitmap.BmpSlice
import korlibs.korge.view.Image
import korlibs.korge.view.anchor
import korlibs.korge.view.position
import korlibs.korge.view.size
import korlibs.math.geom.Point
import tfr.korge.jam.roguymaze.math.PositionGrid
import tfr.korge.jam.roguymaze.model.Tile

class WorldImage(tileSize: Number,
        val position: Point,
        val image: BmpSlice,
        val tile: Tile,
        val gridPos: PositionGrid.Position) : Image(image) {
    init {
        anchor(0.5, 0.5)
        size(tileSize, tileSize)
        position(position)
    }

    override fun toString(): String {
        return "\n\n $tile: ($x,$y) [$gridPos]"
    }
}