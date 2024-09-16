package tfr.korge.jam.roguymaze.lib

import korlibs.math.geom.Point

data class Resolution(val width: Int, val height: Int) {
    fun center() = Point(width / 2, height / 2)
}