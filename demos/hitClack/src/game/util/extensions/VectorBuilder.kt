package de.tfr.game.util.extensions

import de.tfr.game.lib.actor.Point
import korlibs.math.geom.vector.VectorBuilder

fun VectorBuilder.square(point: Point, a: Double) {
    this.rect(point.x, point.y, a, a)
}