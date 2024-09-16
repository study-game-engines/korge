package j4k.candycrush.renderer.animation

import korlibs.korge.tween.tween
import korlibs.korge.view.View
import korlibs.math.geom.Point
import korlibs.math.interpolation.Easing
import korlibs.time.TimeSpan

suspend fun View.move(point: Point, settings: AnimationSettings) {
    return move(point, settings.time, settings.easing)
}

suspend fun View.move(point: Point, time: TimeSpan, easing: Easing) {
    return this.tween(this::x[point.x], this::y[point.y], time = time, easing = easing)
}


class AnimationSettings(val time: TimeSpan, val easing: Easing)