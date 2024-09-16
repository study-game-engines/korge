package tfr.korge.jam.roguymaze.renderer.animation

import korlibs.korge.tween.tween
import korlibs.korge.view.View
import korlibs.math.geom.Point
import korlibs.math.interpolation.Easing
import korlibs.time.TimeSpan
import kotlin.time.Duration


suspend fun View.move(point: Point, settings: AnimationSettings) {
    return move(point, settings.time, settings.easing)
}

suspend fun View.move(point: Point, time: Duration, easing: Easing) {
    return this.tween(this::x[point._x], this::y[point._y], time = time, easing = easing)
}


class AnimationSettings(val time: TimeSpan, val easing: Easing)