package com.esotericsoftware.spine.attachments

import korlibs.image.color.RGBAf

// An attachment with vertices that make up a polygon. Can be used for hit detection, creating physics bodies, spawning particle effects, and more.
class BoundingBoxAttachment(name: String) : VertexAttachment(name) {

    val color: RGBAf = RGBAf(0.38f, 0.94f, 0f, 1f) // #60f000ff

    override fun copy(): Attachment {
        val copy: BoundingBoxAttachment = BoundingBoxAttachment(name)
        copyTo(copy)
        copy.color.setTo(color)
        return copy
    }

}
