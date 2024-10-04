package com.esotericsoftware.spine.effect

import korlibs.image.color.RGBAf
import com.esotericsoftware.spine.utils.SpineVector2
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.utils.SpineUtils
import kotlin.math.*

class SwirlEffect(private var radius: Float) : VertexEffect {

    private var worldX: Float = 0.toFloat()
    private var worldY: Float = 0.toFloat()
    private var angle: Float = 0.toFloat()
    var interpolation: (Float) -> Float = { a ->
        val power: Int = 2
        (a - 1.toDouble()).pow(power.toDouble()).toFloat() * (if (power % 2 == 0) -1 else 1) + 1
    }
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()

    fun ((Float) -> Float).apply(start: Float, end: Float, a: Float): Float {
        return start + (end - start) * this(a)
    }

    override fun begin(skeleton: Skeleton) {
        worldX = skeleton.x + centerX
        worldY = skeleton.y + centerY
    }

    override fun transform(position: SpineVector2, uv: SpineVector2, light: RGBAf, dark: RGBAf) {
        val x: Float = position.x - worldX
        val y: Float = position.y - worldY
        val dist = sqrt((x * x + y * y).toDouble()).toFloat()
        if (dist < radius) {
            val theta = interpolation.apply(0f, angle, (radius - dist) / radius)
            val cos = SpineUtils.cos(theta)
            val sin = SpineUtils.sin(theta)
            position.x = cos * x - sin * y + worldX
            position.y = sin * x + cos * y + worldY
        }
    }

    override fun end() {}

    fun setRadius(radius: Float) {
        this.radius = radius
    }

    fun setCenter(centerX: Float, centerY: Float) {
        this.centerX = centerX
        this.centerY = centerY
    }

    fun setCenterX(centerX: Float) {
        this.centerX = centerX
    }

    fun setCenterY(centerY: Float) {
        this.centerY = centerY
    }

    fun setAngle(degrees: Float) {
        this.angle = degrees * SpineUtils.degRad
    }

}
