package com.esotericsoftware.spine.attachments

import korlibs.image.color.RGBAf

import com.esotericsoftware.spine.PathConstraint
import com.esotericsoftware.spine.utils.SpineUtils.arraycopy

// An attachment whose vertices make up a composite Bezier curve.
// http://esotericsoftware.com/spine-paths
class PathAttachment(name: String) : VertexAttachment(name) {

    lateinit var lengths: FloatArray // The lengths along the path in the setup pose from the start of the path to the end of each Bezier curve.
    var closed: Boolean = false // If true, the start and end knots are connected.
    var constantSpeed: Boolean = false // If true, additional calculations are performed to make calculating positions along the path more accurate. If false, fewer calculations are performed but calculating positions along the path is less accurate.
    val color: RGBAf = RGBAf(1f, 0.5f, 0f, 1f) // The color of the path as it was in Spine. Available only when nonessential data was exported. Paths are not usually rendered at runtime.

    override fun copy(): Attachment {
        val copy: PathAttachment = PathAttachment(name)
        copyTo(copy)
        copy.lengths = FloatArray(lengths.size)
        arraycopy(lengths, 0, copy.lengths, 0, lengths.size)
        copy.closed = closed
        copy.constantSpeed = constantSpeed
        copy.color.setTo(color)
        return copy
    }

}
