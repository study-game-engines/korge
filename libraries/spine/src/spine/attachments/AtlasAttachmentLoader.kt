package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.Skin
import com.esotericsoftware.spine.SpineRegion
import korlibs.image.atlas.Atlas

// An [AttachmentLoader] that configures attachments using texture regions from an [Atlas].
// See [Loading skeleton data](http://esotericsoftware.com/spine-loading-skeleton-data#JSON-and-binary-data) in the Spine Runtimes Guide
class AtlasAttachmentLoader(private val atlas: Atlas) : AttachmentLoader {

    private val regions: HashMap<String, SpineRegion> = HashMap()

    private fun findRegion(path: String): SpineRegion {
        return regions.getOrPut(path) {
            val entry: Atlas.Entry = atlas.tryGetEntryByName(path) ?: error("Can't find '$path' in atlas")
            SpineRegion(entry)
        }
    }

    override fun newRegionAttachment(skin: Skin, name: String, path: String): RegionAttachment {
        val region: SpineRegion = findRegion(path)
        val attachment: RegionAttachment = RegionAttachment(name)
        attachment.region = region
        return attachment
    }

    override fun newMeshAttachment(skin: Skin, name: String, path: String): MeshAttachment? {
        val region: SpineRegion = findRegion(path)
        val attachment: MeshAttachment = MeshAttachment(name)
        attachment.region = region
        return attachment
    }

    override fun newBoundingBoxAttachment(skin: Skin, name: String): BoundingBoxAttachment? {
        return BoundingBoxAttachment(name)
    }

    override fun newClippingAttachment(skin: Skin, name: String): ClippingAttachment? {
        return ClippingAttachment(name)
    }

    override fun newPathAttachment(skin: Skin, name: String): PathAttachment? {
        return PathAttachment(name)
    }

    override fun newPointAttachment(skin: Skin, name: String): PointAttachment? {
        return PointAttachment(name)
    }

}
