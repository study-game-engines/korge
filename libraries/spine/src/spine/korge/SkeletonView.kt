package com.esotericsoftware.spine.korge

import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.BlendMode
import com.esotericsoftware.spine.Skeleton
import com.esotericsoftware.spine.Slot
import com.esotericsoftware.spine.attachments.ClippingAttachment
import com.esotericsoftware.spine.attachments.MeshAttachment
import com.esotericsoftware.spine.attachments.RegionAttachment
import com.esotericsoftware.spine.attachments.SkeletonAttachment
import com.esotericsoftware.spine.effect.VertexEffect
import com.esotericsoftware.spine.utils.SkeletonClipping
import com.esotericsoftware.spine.utils.SpineVector2
import com.esotericsoftware.spine.utils.setSize
import com.esotericsoftware.spine.utils.toArray
import korlibs.datastructure.FastArrayList
import korlibs.datastructure.FloatArrayList
import korlibs.datastructure.iterators.fastForEach
import korlibs.image.bitmap.Bitmap
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.color.RGBAf
import korlibs.korge.render.RenderContext
import korlibs.korge.view.*
import korlibs.korge.view.property.ViewProperty
import korlibs.korge.view.property.ViewPropertyProvider
import korlibs.math.clamp01
import korlibs.math.convertRange
import korlibs.math.geom.BoundsBuilder
import korlibs.math.geom.MBoundsBuilder
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.interpolation.Ratio
import korlibs.time.TimeSpan
import korlibs.time.milliseconds
import korlibs.time.seconds

inline fun Container.skeletonView(skeleton: Skeleton, animationState: AnimationState, block: @ViewDslMarker SkeletonView.() -> Unit = {}): SkeletonView {
    return SkeletonView(skeleton, animationState).addTo(this, block)
}

class SkeletonView(val skeleton: Skeleton, val animationState: AnimationState?) : View() {


    @ViewProperty
    var ratio: Ratio = Ratio.ZERO
        set(value) {
            field = value
            running = false
            animationState?.tracks?.filterNotNull()?.fastForEach {
                it.trackTime = value.toFloat().clamp01().convertRange(0f, 1f, it.animationStart, it.animationEnd - 0.01f)
            }
        }

    var running: Boolean = true

    init {
        if (animationState != null) {
            addUpdater { delta ->
                update(delta)
            }
        }
    }

    @ViewProperty
    fun play() {
        running = true
    }

    @ViewProperty
    fun stop() {
        running = false
    }

    fun update(delta: TimeSpan) {
        if (running) {
            animationState?.update(delta.seconds.toFloat())
            if (delta != 0.milliseconds) {
                invalidate() // @TODO: We should check if we updated something, to mark the view as invalidated
            }
        }
        animationState?.apply(skeleton)
    }

    fun updateAdjustSkeleton() {
        skeleton.setPosition(0f, 0f)
        skeleton.updateWorldTransform()
    }

    override fun renderInternal(ctx: RenderContext) {
        updateAdjustSkeleton()
        renderSkeleton(ctx, skeleton, null)
    }

    var premultipliedAlpha: Boolean = false
    private val vertices: FloatArrayList = FloatArrayList(32)
    private val clipper: SkeletonClipping = SkeletonClipping()
    var vertexEffect: VertexEffect? = null
    private val temp: SpineVector2 = SpineVector2()
    private val temp2: SpineVector2 = SpineVector2()
    private val temp3: RGBAf = RGBAf()
    private val temp4: RGBAf = RGBAf()
    private val temp5: RGBAf = RGBAf()
    private val temp6: RGBAf = RGBAf()
    private var currentSpineBlendMode: BlendMode? = null

    private fun renderSkeleton(ctx: RenderContext?, skeleton: Skeleton, bb: BoundsBuilder?) {
        val tempPosition: SpineVector2 = this.temp
        val tempUV: SpineVector2 = this.temp2
        val tempLight1: RGBAf = this.temp3
        val tempDark1: RGBAf = this.temp4
        val tempLight2: RGBAf = this.temp5
        val tempDark2: RGBAf = this.temp6
        val vertexEffect: VertexEffect? = this.vertexEffect
        vertexEffect?.begin(skeleton)
        val premultipliedAlpha: Boolean = this.premultipliedAlpha
        var blendMode: BlendMode? = null
        var verticesLength: Int = 0
        lateinit var vertices: FloatArray
        var uvs: FloatArray? = null
        lateinit var triangles: ShortArray
        var color: RGBAf? = null
        val skeletonColor: RGBAf = skeleton.color
        val r: Float = skeletonColor.r
        val g: Float = skeletonColor.g
        val b: Float = skeletonColor.b
        val a: Float = skeletonColor.a
        val drawOrder: FastArrayList<Slot> = skeleton.drawOrder
        var i: Int = 0
        val n: Int = drawOrder.size
        while (i < n) {
            val slot = drawOrder[i]
            if (!slot.bone.isActive) {
                clipper.clipEnd(slot)
                i++
                continue
            }
            var texture: Bitmap? = null
            val vertexSize = if (clipper.isClipping) 2 else 5
            val attachment = slot.attachment
            if (attachment is RegionAttachment) {
                verticesLength = vertexSize shl 2
                vertices = this.vertices.data
                attachment.computeWorldVertices(slot.bone, vertices, 0, vertexSize)
                triangles = quadTriangles
                texture = attachment.region.texture
                uvs = attachment.uVs
                color = attachment.color

            } else if (attachment is MeshAttachment) {
                val count = attachment.worldVerticesLength
                verticesLength = (count shr 1) * vertexSize
                vertices = this.vertices.setSize(verticesLength)
                attachment.computeWorldVertices(slot, 0, count, vertices, 0, vertexSize)
                triangles = attachment.triangles
                texture = attachment.region!!.texture
                uvs = attachment.uVs
                color = attachment.color

            } else if (attachment is ClippingAttachment) {
                clipper.clipStart(slot, attachment)
                i++
                continue

            } else if (attachment is SkeletonAttachment) {
                val attachmentSkeleton = attachment.skeleton
                if (attachmentSkeleton != null) renderSkeleton(ctx, attachmentSkeleton, bb)
            }

            if (texture != null) {
                val slotColor = slot.color
                var alpha = a * slotColor.a * color!!.a * 255f
                val multiplier = if (premultipliedAlpha) alpha else 255f

                var slotBlendMode = slot.data.getBlendMode()
                if (slotBlendMode != blendMode) {
                    if (slotBlendMode == BlendMode.additive && premultipliedAlpha) {
                        slotBlendMode = BlendMode.normal
                        alpha = 0f
                    }
                    blendMode = slotBlendMode
                    currentSpineBlendMode = blendMode
                }

                val c = RGBA(
                    (r * slotColor.r * color.r * multiplier).toInt(),
                    (g * slotColor.g * color.g * multiplier).toInt(),
                    (b * slotColor.b * color.b * multiplier).toInt(),
                    alpha.toInt()
                )

                if (clipper.isClipping) {
                    clipper.clipTriangles(vertices, verticesLength, triangles, triangles.size, uvs!!, c, Colors.BLACK, false)
                    val clippedVertices = clipper.clippedVertices
                    val clippedTriangles = clipper.clippedTriangles
                    if (vertexEffect != null) applyVertexEffect(clippedVertices.data, clippedVertices.size, 5, c, Colors.BLACK)
                    draw(bb, ctx, texture, clippedVertices.data, 0, clippedVertices.size, clippedTriangles.toArray(), 0, clippedTriangles.size, currentSpineBlendMode)
                } else {
                    if (vertexEffect != null) {
                        tempLight1.setTo(c)
                        tempDark1.setTo(Colors.BLACK)
                        var v = 0
                        var u = 0
                        while (v < verticesLength) {
                            tempPosition.x = vertices!![v]
                            tempPosition.y = vertices[v + 1]
                            tempLight2.setTo(tempLight1)
                            tempDark2.setTo(tempDark1)
                            tempUV.x = uvs!![u]
                            tempUV.y = uvs[u + 1]
                            vertexEffect.transform(tempPosition, tempUV, tempLight2, tempDark2)
                            vertices[v + 0] = tempPosition.x
                            vertices[v + 1] = tempPosition.y
                            vertices[v + 2] = tempLight2.toFloatBits()
                            vertices[v + 3] = tempUV.x
                            vertices[v + 4] = tempUV.y
                            v += 5
                            u += 2
                        }
                    } else {
                        var v = 0
                        var u = 0
                        while (v < verticesLength) {
                            vertices[v + 2] = Float.fromBits(c.value)
                            vertices[v + 3] = uvs!![u]
                            vertices[v + 4] = uvs[u + 1]
                            v += 5
                            u += 2
                        }
                    }
                    draw(bb, ctx, texture, vertices, 0, verticesLength, triangles, 0, triangles.size, currentSpineBlendMode)
                }

                //break // @TODO: Remove this
            }

            clipper.clipEnd(slot)
            i++

        }
        clipper.clipEnd()
        vertexEffect?.end()
    }

    private fun applyVertexEffect(vertices: FloatArray, verticesLength: Int, stride: Int, light: RGBA, dark: RGBA) {
        val tempPosition: SpineVector2 = this.temp
        val tempUV: SpineVector2 = this.temp2
        val tempLight1: RGBAf = this.temp3
        val tempDark1: RGBAf = this.temp4
        val tempLight2: RGBAf = this.temp5
        val tempDark2: RGBAf = this.temp6
        val vertexEffect: VertexEffect? = this.vertexEffect
        tempLight1.setTo(light)
        tempDark1.setTo(dark)
        if (stride == 5) {
            var v: Int = 0
            while (v < verticesLength) {
                tempPosition.x = vertices[v]
                tempPosition.y = vertices[v + 1]
                tempUV.x = vertices[v + 3]
                tempUV.y = vertices[v + 4]
                tempLight2.setTo(tempLight1)
                tempDark2.setTo(tempDark1)
                vertexEffect!!.transform(tempPosition, tempUV, tempLight2, tempDark2)
                vertices[v] = tempPosition.x
                vertices[v + 1] = tempPosition.y
                vertices[v + 2] = tempLight2.toFloatBits()
                vertices[v + 3] = tempUV.x
                vertices[v + 4] = tempUV.y
                v += stride
            }
        } else {
            var v: Int = 0
            while (v < verticesLength) {
                tempPosition.x = vertices[v]
                tempPosition.y = vertices[v + 1]
                tempUV.x = vertices[v + 4]
                tempUV.y = vertices[v + 5]
                tempLight2.setTo(tempLight1)
                tempDark2.setTo(tempDark1)
                vertexEffect!!.transform(tempPosition, tempUV, tempLight2, tempDark2)
                vertices[v] = tempPosition.x
                vertices[v + 1] = tempPosition.y
                vertices[v + 2] = tempLight2.toFloatBits()
                vertices[v + 3] = tempDark2.toFloatBits()
                vertices[v + 4] = tempUV.x
                vertices[v + 5] = tempUV.y
                v += stride
            }
        }
    }

    private fun draw(bb: BoundsBuilder?, ctx: RenderContext?, texture: Bitmap, verticesData: FloatArray, verticesOffset: Int, verticesCount: Int, triangle: ShortArray, trianglesOffset: Int, trianglesCount: Int, blendMode: BlendMode?) {
        val vertexSize = 5
        val vertexCount = verticesCount / vertexSize
        if (bb != null) {
            for (n in 0 until vertexCount) {
                val index = n * vertexSize
                val x = verticesData[index + 0]
                val y = verticesData[index + 1]
                bb + Point(x, -y)
            }
        }
        ctx?.useBatcher { batch ->
            val viewBlendMode = when (blendMode) {
                BlendMode.normal -> korlibs.korge.view.BlendMode.NORMAL
                BlendMode.additive -> korlibs.korge.view.BlendMode.ADD
                BlendMode.multiply -> korlibs.korge.view.BlendMode.MULTIPLY
                BlendMode.screen -> korlibs.korge.view.BlendMode.SCREEN
                null -> this.blendMode
            }

            batch.setStateFast(texture, true, viewBlendMode, null, icount = trianglesCount, vcount = vertexCount)

            val transform = this.globalMatrix
            val premultiplied = texture.premultiplied

            for (n in 0 until trianglesCount) {
                batch.addIndexRelative(triangle[trianglesOffset + n].toInt())
            }
            val colorMul = this.renderColorMul
            for (n in 0 until vertexCount) {
                val x = verticesData[verticesOffset + n * vertexSize + 0]
                val y = -verticesData[verticesOffset + n * vertexSize + 1]
                val u = verticesData[verticesOffset + n * vertexSize + 3]
                val v = verticesData[verticesOffset + n * vertexSize + 4]
                val realX = transform.transformX(x, y)
                val realY = transform.transformY(x, y)
                batch.addVertex(
                    realX, realY, u, v, colorMul,
                    //colorAdd, premultiplied = premultiplied, wrap = false
                )
            }

            //batch.flush()
        }
    }

    private val bb: BoundsBuilder = BoundsBuilder()

    override fun getLocalBoundsInternal(): Rectangle {
        updateAdjustSkeleton()
        renderSkeleton(null, skeleton, bb)
        return bb.bounds
    }

    // @TODO: We shouldn't do this
    private fun RGBAf.toFloatBits(): Float = Float.fromBits(this.rgba.value)

    val currentMainAnimation get() = animationState?.tracks?.first()?.animation

    @Suppress("unused")
    object AnimationNameProvider : ViewPropertyProvider.ListImpl<SkeletonView, String>() {
        override fun listProvider(instance: SkeletonView): List<String> =
            instance.skeleton.data.animations.map { it.name }
    }

    @ViewProperty
    @ViewPropertyProvider(AnimationNameProvider::class)
    @Suppress("unused")
    var animationName: String
        get() = currentMainAnimation?.name ?: "default"
        set(animationName) {
            val animation = skeleton.data.findAnimation(animationName)
            if (animation != null) {
                this@SkeletonView.play()
                animationState?.setAnimation(0, animation, true)
                stage?.views?.debugHightlightView(this@SkeletonView)
            }
        }

    //override fun buildDebugComponent(views: Views, container: UiContainer) {
    //    container.uiCollapsibleSection("Animation") {
    //        addChild(UiRowEditableValue(app, "animation", UiListEditableValue(app, { skeleton.data.animations.map { it.name } }, ObservableProperty(
    //            name = "animation",
    //            internalSet = {  animationName ->
    //                val animation = skeleton.data.findAnimation(animationName)
    //                if (animation != null) {
    //                    this@SkeletonView.play()
    //                    animationState?.setAnimation(0, animation, true)
    //                    stage?.views?.debugHightlightView(this@SkeletonView)
    //                }
    //            },
    //            internalGet = { currentMainAnimation?.name ?: "default" }
    //        ))))
    //        button("play").onClick { play() }
    //        button("stop").onClick { stop() }
    //    }
    //    super.buildDebugComponent(views, container)
    //}

    companion object {
        private val quadTriangles = shortArrayOf(0, 1, 2, 2, 3, 0)
    }

}
