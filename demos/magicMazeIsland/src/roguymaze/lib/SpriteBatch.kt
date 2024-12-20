package tfr.korge.jam.roguymaze.lib

import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.Bitmaps
import korlibs.image.bitmap.BmpSlice
import korlibs.image.bitmap.sliceWithSize
import tfr.korge.jam.roguymaze.math.PositionGrid

/**
 * A sprite batch slices a big image into several sub-images.
 */
open class SpriteBatch(x: Int = 0,
        y: Int = 0,
        spriteSize: Int = 16,
        columns: Int,
        rows: Int,
        private val bitmap: Bitmap) {

    private val defaultSprite = Bitmaps.transparent
    private var sprites: MutableList<BmpSlice> = mutableListOf(defaultSprite)

    private val grid = PositionGrid(x = x, y = y, columns = columns, rows = rows, tileSize = spriteSize)

    operator fun get(spriteIndex: Int) = sprites[spriteIndex]

    init {
        prepareElement()
    }

    private fun prepareElement() {
        repeat(grid.rows) { row ->
            repeat(grid.columns) { column ->
                val pos = grid.getPosition(column, row)
                addSpriteToList(bitmap.sliceWithSize(pos.x.toInt(), pos.y.toInt(), grid.tileSize, grid.tileSize))
            }

        }
    }

    private fun addSpriteToList(sprite: BmpSlice) {
        sprites.add(sprite)
        if (sprites.first() == defaultSprite) {
            sprites.removeAt(0)
        }
    }

}
