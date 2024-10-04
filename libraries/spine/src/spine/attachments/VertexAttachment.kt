package com.esotericsoftware.spine.attachments

import com.esotericsoftware.spine.Bone
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.utils.*
import com.esotericsoftware.spine.utils.SpineUtils.arraycopy
import korlibs.datastructure.FastArrayList
import korlibs.datastructure.FloatArrayList
import korlibs.io.concurrent.atomic.*
import kotlin.jvm.*
import kotlin.native.concurrent.*

// Base class for an attachment with vertices that are transformed by one or more bones and can be deformed by a slot's
abstract class VertexAttachment(name: String) : Attachment(name) {

    val id = nextID() and 65535 shl 11 // Returns a unique ID for this attachment.

    /** The bones which affect the [.getVertices]. The array entries are, for each vertex, the number of bones affecting
     * the vertex followed by that many bone indices, which is the index of the bone in [Skeleton.getBones]. Will be null
     * if this attachment has no weights.  */
    /** @param bones May be null if this attachment has no weights.
     */
    var bones: IntArray? = null
    var vertices: FloatArray? =
        null // The vertex positions in the bone's coordinate system. For a non-weighted attachment, the values are `x,y` entries for each vertex. For a weighted attachment, the values are `x,y,weight` entries for each bone affecting each vertex.
    var worldVerticesLength: Int = 0 // The maximum number of world vertex values that can be output by [.computeWorldVertices] using the `count` parameter.
    var deformAttachment: VertexAttachment = this // Deform keys for the deform attachment are also applied to this attachment. May be null if no deform keys should be applied.

    /**
     * Transforms the attachment's local [.getVertices] to world coordinates. If the slot's [Slot.getDeform] is not empty, it is used to deform the vertices.
     * See [World transforms](http://esotericsoftware.com/spine-runtime-skeletons#World-transforms) in the Spine
     * Runtimes Guide.
     * @param start The index of the first [.getVertices] value to transform. Each vertex has 2 values, x and y.
     * @param count The number of world vertex values to output. Must be <= [.getWorldVerticesLength] - `start`.
     * @param worldVertices The output world vertices. Must have a length >= `offset` + `count` *
     * `stride` / 2.
     * @param offset The `worldVertices` index to begin writing values.
     * @param stride The number of `worldVertices` entries between the value pairs written.
     */
    fun computeWorldVertices(slot: Slot, start: Int, count: Int, worldVertices: FloatArray, offset: Int, stride: Int) {
        var count: Int = count
        count = offset + (count shr 1) * stride
        val skeleton: Skeleton = slot.skeleton
        val deformArray: FloatArrayList = slot.deform
        var vertices: FloatArray? = this.vertices
        val bones: IntArray? = this.bones
        if (bones == null) {
            if (deformArray.size > 0) vertices = deformArray.data
            val bone: Bone = slot.bone
            val x: Float = bone.worldX
            val y: Float = bone.worldY
            val a: Float = bone.a
            val b: Float = bone.b
            val c: Float = bone.c
            val d: Float = bone.d
            var v: Int = start
            var w: Int = offset
            while (w < count) {
                val vx: Float = vertices!![v]
                val vy: Float = vertices[v + 1]
                worldVertices[w] = vx * a + vy * b + x
                worldVertices[w + 1] = vx * c + vy * d + y
                v += 2
                w += stride
            }
            return
        }
        var v: Int = 0
        var skip: Int = 0
        var index: Int = 0
        while (index < start) {
            val n = bones[v]
            v += n + 1
            skip += n
            index += 2
        }
        val skeletonBones: FastArrayList<Bone> = skeleton.bones
        if (deformArray.size == 0) {
            var w: Int = offset
            var b: Int = skip * 3
            while (w < count) {
                var wx: Float = 0f
                var wy: Float = 0f
                var n: Int = bones[v++]
                n += v
                while (v < n) {
                    val bone: Bone = skeletonBones[bones[v]] as Bone
                    val vx: Float = vertices!![b]
                    val vy: Float = vertices[b + 1]
                    val weight: Float = vertices[b + 2]
                    wx += (vx * bone.a + vy * bone.b + bone.worldX) * weight
                    wy += (vx * bone.c + vy * bone.d + bone.worldY) * weight
                    v++
                    b += 3
                }
                worldVertices[w] = wx
                worldVertices[w + 1] = wy
                w += stride
            }
        } else {
            val deform: FloatArray = deformArray.data
            var w: Int = offset
            var b: Int = skip * 3
            var f: Int = skip shl 1
            while (w < count) {
                var wx: Float = 0f
                var wy: Float = 0f
                var n: Int = bones[v++]
                n += v
                while (v < n) {
                    val bone: Bone = skeletonBones[bones[v]] as Bone
                    val vx: Float = vertices!![b] + deform[f]
                    val vy: Float = vertices[b + 1] + deform[f + 1]
                    val weight: Float = vertices[b + 2]
                    wx += (vx * bone.a + vy * bone.b + bone.worldX) * weight
                    wy += (vx * bone.c + vy * bone.d + bone.worldY) * weight
                    v++
                    b += 3
                    f += 2
                }
                worldVertices[w] = wx
                worldVertices[w + 1] = wy
                w += stride
            }
        }
    }

    // Does not copy id (generated) or name (set on construction)
    internal fun copyTo(attachment: VertexAttachment) {
        if (bones != null) {
            attachment.bones = IntArray(bones!!.size)
            arraycopy(bones!!, 0, attachment.bones!!, 0, bones!!.size)
        } else {
            attachment.bones = null
        }
        if (vertices != null) {
            attachment.vertices = FloatArray(vertices!!.size)
            arraycopy(vertices!!, 0, attachment.vertices!!, 0, vertices!!.size)
        } else {
            attachment.vertices = null
        }
        attachment.worldVerticesLength = worldVerticesLength
        attachment.deformAttachment = deformAttachment
    }

    companion object {
        private fun nextID(): Int = nextID++
    }

}

// @TODO: Do this properly.
@Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")
@ThreadLocal
//private var nextID = korAtomic(0)
private var nextID = 0
