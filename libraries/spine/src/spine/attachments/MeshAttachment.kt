package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.SpineRegion
import com.esotericsoftware.spine.utils.SpineUtils.arraycopy
import korlibs.image.color.*

/**
 * An attachment that displays a textured mesh. A mesh has hull vertices and internal vertices within the hull. Holes are not
 * supported. Each vertex has UVs (texture coordinates) and triangles are used to map an image on to the mesh.
 */
// http://esotericsoftware.com/spine-meshes
class MeshAttachment(name: String) : VertexAttachment(name) {

    var region: SpineRegion? = null


    var path: String? = null // The name of the texture region for this attachment
    /** The UV pair for each vertex, normalized within the texture region.  */
    lateinit var regionUVs: FloatArray // Sets the texture coordinates for the region. The values are u,v pairs for each vertex.
    var uVs: FloatArray? = null // The UV pair for each vertex, normalized within the entire texture.
    lateinit var triangles: ShortArray // Triplets of vertex indices which describe the mesh's triangulation.
    val color = RGBAf(1f, 1f, 1f, 1f) // The color to tint the mesh.
    var hullLength: Int = 0 // The number of entries at the beginning of [.vertices] that make up the mesh hull.

    /** The parent mesh if this is a linked mesh, else null. A linked mesh shares the [.bones], [.vertices],
     * [.regionUVs], [.triangles], [.hullLength], [.edges], [.width], and [.height] with the
     * parent mesh, but may have a different [.name] or [.path] (and therefore a different texture).  */
    /** @param parentMesh May be null.
     */
    var parentMesh: MeshAttachment? = null
        set(parentMesh) {
            field = parentMesh
            if (parentMesh != null) {
                bones = parentMesh.bones
                vertices = parentMesh.vertices
                regionUVs = parentMesh.regionUVs
                triangles = parentMesh.triangles
                hullLength = parentMesh.hullLength
                worldVerticesLength = parentMesh.worldVerticesLength
                edges = parentMesh.edges
                width = parentMesh.width
                height = parentMesh.height
            }
        }

    var edges: ShortArray? = null // Vertex index pairs describing edges for controling triangulation. Mesh triangles will never cross edges. Only available if nonessential data was exported. Triangulation is not performed at runtime
    var width: Float = 0.toFloat() // The width of the mesh's image. Available only when nonessential data was exported.
    var height: Float = 0.toFloat() // The height of the mesh's image. Available only when nonessential data was exported.

    // Calculates [.uvs] using [.regionUVs] and the [.region]. Must be called after changing the region UVs or region
    fun updateUVs() {
        val regionUVs = this.regionUVs
        if (this.uVs == null || this.uVs!!.size != regionUVs.size) this.uVs = FloatArray(regionUVs.size)
        val uvs = this.uVs
        val n = uvs!!.size
        var u: Float
        var v: Float
        val width: Float
        val height: Float
        if (region is SpineRegion) {
            u = region!!.u
            v = region!!.v
            val region = this.region as SpineRegion?
            val textureWidth = region!!.texture.width
            val textureHeight = region.texture.height
            when (region.degrees) {
                90 -> {
                    u -= (region.originalHeight - region.offsetY - region.packedWidth) / textureWidth
                    v -= (region.originalWidth - region.offsetX - region.packedHeight) / textureHeight
                    width = region.originalHeight / textureWidth
                    height = region.originalWidth / textureHeight
                    run {
                        var i = 0
                        while (i < n) {
                            uvs[i] = u + regionUVs[i + 1] * width
                            uvs[i + 1] = v + (1 - regionUVs[i]) * height
                            i += 2
                        }
                    }
                    return
                }
                180 -> {
                    u -= (region.originalWidth - region.offsetX - region.packedWidth) / textureWidth
                    v -= region.offsetY / textureHeight
                    width = region.originalWidth / textureWidth
                    height = region.originalHeight / textureHeight
                    run {
                        var i = 0
                        while (i < n) {
                            uvs[i] = u + (1 - regionUVs[i]) * width
                            uvs[i + 1] = v + (1 - regionUVs[i + 1]) * height
                            i += 2
                        }
                    }
                    return
                }
                270 -> {
                    u -= region.offsetY / textureWidth
                    v -= region.offsetX / textureHeight
                    width = region.originalHeight / textureWidth
                    height = region.originalWidth / textureHeight
                    var i = 0
                    while (i < n) {
                        uvs[i] = u + (1 - regionUVs[i + 1]) * width
                        uvs[i + 1] = v + regionUVs[i] * height
                        i += 2
                    }
                    return
                }
            }
            u -= region.offsetX / textureWidth
            v -= (region.originalHeight - region.offsetY - region.packedHeight) / textureHeight
            width = region.originalWidth / textureWidth
            height = region.originalHeight / textureHeight
        } else if (region == null) {
            v = 0f
            u = v
            height = 1f
            width = height
        } else {
            u = region!!.u
            v = region!!.v
            width = region!!.u2 - u
            height = region!!.v2 - v
        }
        var index: Int = 0
        while (index < n) {
            uvs[index] = u + regionUVs[index] * width
            uvs[index + 1] = v + regionUVs[index + 1] * height
            index += 2
        }
    }

    override fun copy(): Attachment {
        if (this.parentMesh != null) return newLinkedMesh()

        val copy: MeshAttachment = MeshAttachment(name)
        copy.region = region
        copy.path = path
        copy.color.setTo(color)

        copyTo(copy)
        copy.regionUVs = FloatArray(regionUVs.size)
        arraycopy(regionUVs, 0, copy.regionUVs, 0, regionUVs.size)
        copy.uVs = FloatArray(uVs!!.size)
        arraycopy(uVs!!, 0, copy.uVs!!, 0, uVs!!.size)
        copy.triangles = ShortArray(triangles.size)
        arraycopy(triangles, 0, copy.triangles, 0, triangles.size)
        copy.hullLength = hullLength

        // Nonessential.
        if (edges != null) {
            copy.edges = ShortArray(edges!!.size)
            arraycopy(edges!!, 0, copy.edges!!, 0, edges!!.size)
        }
        copy.width = width
        copy.height = height
        return copy
    }

    // Returns a new mesh with the [.parentMesh] set to this mesh's parent mesh, if any, else to this mesh
    fun newLinkedMesh(): MeshAttachment {
        val mesh: MeshAttachment = MeshAttachment(name)
        mesh.region = region
        mesh.path = path
        mesh.color.setTo(color)
        mesh.deformAttachment = deformAttachment
        mesh.parentMesh = if (this.parentMesh != null) this.parentMesh else this
        mesh.updateUVs()
        return mesh
    }

}
