package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.Skeleton

// Attachment that displays a skeleton
class SkeletonAttachment(name: String) : Attachment(name) {

    var skeleton: Skeleton? = null

    override fun copy(): Attachment {
        val copy: SkeletonAttachment = SkeletonAttachment(name)
        copy.skeleton = skeleton
        return copy
    }

}
