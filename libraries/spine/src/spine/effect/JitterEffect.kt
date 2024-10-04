package com.esotericsoftware.spine.effect

import korlibs.image.color.RGBAf
import com.esotericsoftware.spine.utils.SpineVector2
import com.esotericsoftware.spine.Skeleton
import kotlin.random.Random

class JitterEffect(private var x: Float, private var y: Float, val random: Random = Random) : VertexEffect {

    override fun begin(skeleton: Skeleton) {
        // no op
    }

    private fun randomTriangular(min: Float, max: Float, mode: Float = (min + max) * 0.5f): Float {
        val u: Float = random.nextFloat()
        val d: Float = max - min
        return if (u <= (mode - min) / d) min + kotlin.math.sqrt(u * d * (mode - min).toDouble()).toFloat() else max - kotlin.math.sqrt((1 - u) * d * (max - mode).toDouble()).toFloat()
    }

    override fun transform(position: SpineVector2, uv: SpineVector2, light: RGBAf, dark: RGBAf) {
        position.x += randomTriangular(-x, y)
        position.y += randomTriangular(-x, y)
    }

    override fun end() {}

    fun setJitter(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setJitterX(x: Float) {
        this.x = x
    }

    fun setJitterY(y: Float) {
        this.y = y
    }

}
