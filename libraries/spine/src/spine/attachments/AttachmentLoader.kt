package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.Skin

interface AttachmentLoader {
    fun newRegionAttachment(skin: Skin, name: String, path: String): RegionAttachment? = null
    fun newMeshAttachment(skin: Skin, name: String, path: String): MeshAttachment? = null
    fun newBoundingBoxAttachment(skin: Skin, name: String): BoundingBoxAttachment? = null
    fun newClippingAttachment(skin: Skin, name: String): ClippingAttachment? = null
    fun newPathAttachment(skin: Skin, name: String): PathAttachment? = null
    fun newPointAttachment(skin: Skin, name: String): PointAttachment? = null
}
