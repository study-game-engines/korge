package com.esotericsoftware.spine.attachments

import korlibs.image.color.RGBAf
import com.esotericsoftware.spine.SlotData

class ClippingAttachment(name: String) : VertexAttachment(name) {

    lateinit var endSlot: SlotData // Clipping is performed between the clipping polygon's slot and the end slot. Returns -1 if clipping is done until the end of the skeleton's rendering
    val color: RGBAf = RGBAf(0.2275f, 0.2275f, 0.8078f, 1f) // The color of the clipping polygon as it was in Spine. Available only when nonessential data was exported. Clipping polygons are not usually rendered at runtime

    override fun copy(): Attachment {
        val copy: ClippingAttachment = ClippingAttachment(name)
        copyTo(copy)
        copy.endSlot = endSlot
        copy.color.setTo(color)
        return copy
    }

}
