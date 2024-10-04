package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.utils.SpineUtils.cosDeg
import com.esotericsoftware.spine.utils.SpineUtils.radDeg
import com.esotericsoftware.spine.utils.SpineUtils.sinDeg

import korlibs.image.color.RGBAf
import com.esotericsoftware.spine.utils.SpineVector2
import com.esotericsoftware.spine.Bone

/**
 * An attachment which is a single point and a rotation. This can be used to spawn projectiles, particles, etc. A bone can be
 * used in similar ways, but a PointAttachment is slightly less expensive to compute and can be hidden, shown, and placed in a
 * skin.
 */
// http://esotericsoftware.com/spine-point-attachments
class PointAttachment(name: String) : Attachment(name) {

    var x: Float = 0.toFloat()
    var y: Float = 0.toFloat()
    var rotation: Float = 0.toFloat()
    val color: RGBAf = RGBAf(0.9451f, 0.9451f, 0f, 1f) // The color of the point attachment as it was in Spine. Available only when nonessential data was exported. Point attachments are not usually rendered at runtime.

    fun computeWorldPosition(bone: Bone, point: SpineVector2): SpineVector2 {
        point.x = x * bone.a + y * bone.b + bone.worldX
        point.y = x * bone.c + y * bone.d + bone.worldY
        return point
    }

    fun computeWorldRotation(bone: Bone): Float {
        val cos: Float = cosDeg(rotation)
        val sin: Float = sinDeg(rotation)
        val x: Float = cos * bone.a + sin * bone.b
        val y: Float = cos * bone.c + sin * bone.d
        return kotlin.math.atan2(y.toDouble(), x.toDouble()).toFloat() * radDeg
    }

    override fun copy(): Attachment {
        val copy: PointAttachment = PointAttachment(name)
        copy.x = x
        copy.y = y
        copy.rotation = rotation
        copy.color.setTo(color)
        return copy
    }

}
