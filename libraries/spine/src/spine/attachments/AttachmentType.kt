package com.esotericsoftware.spine.attachments

enum class AttachmentType {

    region,
    boundingbox,
    mesh,
    linkedmesh,
    path,
    point,
    clipping;

    companion object {
        val values: Array<AttachmentType> = entries.toTypedArray()
    }

}
