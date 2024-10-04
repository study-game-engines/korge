package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.SpineRegion

import com.esotericsoftware.spine.Bone
import com.esotericsoftware.spine.utils.*
import com.esotericsoftware.spine.utils.SpineUtils.arraycopy
import korlibs.image.color.*

// An attachment that displays a textured quadrilateral.
// http://esotericsoftware.com/spine-regions
class RegionAttachment(name: String) : Attachment(name) {

    private var _region: SpineRegion? = null

    var region: SpineRegion
        get() = _region ?: error("Region was not set before")
        set(region) {
            _region = region
            val uvs = this.uVs
            if (region.rotate) {
                uvs[URX] = region.u
                uvs[URY] = region.v2
                uvs[BRX] = region.u
                uvs[BRY] = region.v
                uvs[BLX] = region.u2
                uvs[BLY] = region.v
                uvs[ULX] = region.u2
                uvs[ULY] = region.v2
            } else {
                uvs[ULX] = region.u
                uvs[ULY] = region.v2
                uvs[URX] = region.u
                uvs[URY] = region.v
                uvs[BRX] = region.u2
                uvs[BRY] = region.v
                uvs[BLX] = region.u2
                uvs[BLY] = region.v2
            }
        }

    var path: String? = null // The name of the texture region for this attachment.
    var x: Float = 0.toFloat() // The local x translation.
    var y: Float = 0.toFloat() // The local y translation.
    var scaleX: Float = 1f // The local scaleX.
    var scaleY: Float = 1f // The local scaleY.
    var rotation: Float = 0.toFloat() // The local rotation.
    var width: Float = 0.toFloat() // The width of the region attachment in Spine.
    var height: Float = 0.toFloat() // The height of the region attachment in Spine.
    val uVs: FloatArray = FloatArray(8)
    val offset: FloatArray = FloatArray(8) // For each of the 4 vertices, a pair of `x,y` values that is the local position of the vertex.
    val color: RGBAf = RGBAf(1f, 1f, 1f, 1f) // The color to tint the region attachment.

    // Calculates the [.offset] using the region settings. Must be called after changing region settings
    fun updateOffset() {
        val width: Float = width
        val height: Float = height
        var localX2: Float = width / 2
        var localY2: Float = height / 2
        var localX: Float = -localX2
        var localY: Float = -localY2
        if (region is SpineRegion) {
            val region: SpineRegion? = this.region as SpineRegion?
            localX += region!!.offsetX / region.originalWidth * width
            localY += region.offsetY / region.originalHeight * height
            if (region.rotate) {
                localX2 -= (region.originalWidth - region.offsetX - region.packedHeight) / region.originalWidth * width
                localY2 -= (region.originalHeight - region.offsetY - region.packedWidth) / region.originalHeight * height
            } else {
                localX2 -= (region.originalWidth - region.offsetX - region.packedWidth) / region.originalWidth * width
                localY2 -= (region.originalHeight - region.offsetY - region.packedHeight) / region.originalHeight * height
            }
        }
        val scaleX: Float = scaleX
        val scaleY: Float = scaleY
        localX *= scaleX
        localY *= scaleY
        localX2 *= scaleX
        localY2 *= scaleY
        val rotation: Float = rotation
        val cos: Float = kotlin.math.cos((SpineUtils.degRad * rotation).toDouble()).toFloat()
        val sin: Float = kotlin.math.sin((SpineUtils.degRad * rotation).toDouble()).toFloat()
        val x: Float = x
        val y: Float = y
        val localXCos: Float = localX * cos + x
        val localXSin: Float = localX * sin
        val localYCos: Float = localY * cos + y
        val localYSin: Float = localY * sin
        val localX2Cos: Float = localX2 * cos + x
        val localX2Sin: Float = localX2 * sin
        val localY2Cos: Float = localY2 * cos + y
        val localY2Sin: Float = localY2 * sin
        val offset: FloatArray = this.offset
        offset[BLX] = localXCos - localYSin
        offset[BLY] = localYCos + localXSin
        offset[ULX] = localXCos - localY2Sin
        offset[ULY] = localY2Cos + localXSin
        offset[URX] = localX2Cos - localY2Sin
        offset[URY] = localY2Cos + localX2Sin
        offset[BRX] = localX2Cos - localYSin
        offset[BRY] = localYCos + localX2Sin
    }

    /**
     * Transforms the attachment's four vertices to world coordinates.
     *
     *
     * See [World transforms](http://esotericsoftware.com/spine-runtime-skeletons#World-transforms) in the Spine
     * Runtimes Guide.
     * @param worldVertices The output world vertices. Must have a length >= `offset` + 8.
     * @param offset The `worldVertices` index to begin writing values.
     * @param stride The number of `worldVertices` entries between the value pairs written.
     */
    fun computeWorldVertices(bone: Bone, worldVertices: FloatArray, offset: Int, stride: Int) {
        var offset: Int = offset
        val vertexOffset: FloatArray = this.offset
        val x: Float = bone.worldX
        val y: Float = bone.worldY
        val a: Float = bone.a
        val b: Float = bone.b
        val c: Float = bone.c
        val d: Float = bone.d
        var offsetX: Float
        var offsetY: Float

        offsetX = vertexOffset[BRX]
        offsetY = vertexOffset[BRY]
        worldVertices[offset] = offsetX * a + offsetY * b + x // br
        worldVertices[offset + 1] = offsetX * c + offsetY * d + y
        offset += stride

        offsetX = vertexOffset[BLX]
        offsetY = vertexOffset[BLY]
        worldVertices[offset] = offsetX * a + offsetY * b + x // bl
        worldVertices[offset + 1] = offsetX * c + offsetY * d + y
        offset += stride

        offsetX = vertexOffset[ULX]
        offsetY = vertexOffset[ULY]
        worldVertices[offset] = offsetX * a + offsetY * b + x // ul
        worldVertices[offset + 1] = offsetX * c + offsetY * d + y
        offset += stride

        offsetX = vertexOffset[URX]
        offsetY = vertexOffset[URY]
        worldVertices[offset] = offsetX * a + offsetY * b + x // ur
        worldVertices[offset + 1] = offsetX * c + offsetY * d + y
    }

    override fun copy(): Attachment {
        val copy: RegionAttachment = RegionAttachment(name)
        copy.region = region
        copy.path = path
        copy.x = x
        copy.y = y
        copy.scaleX = scaleX
        copy.scaleY = scaleY
        copy.rotation = rotation
        copy.width = width
        copy.height = height
        arraycopy(uVs, 0, copy.uVs, 0, 8)
        arraycopy(offset, 0, copy.offset, 0, 8)
        copy.color.setTo(color)
        return copy
    }

    companion object {
        val BLX: Int = 0
        val BLY: Int = 1
        val ULX: Int = 2
        val ULY: Int = 3
        val URX: Int = 4
        val URY: Int = 5
        val BRX: Int = 6
        val BRY: Int = 7
    }

}
