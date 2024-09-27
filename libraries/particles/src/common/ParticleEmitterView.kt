package korlibs.korge.particle

import korlibs.graphics.AGBlending
import korlibs.image.bitmap.Bitmaps
import korlibs.image.bitmap.bmp
import korlibs.image.format.readBitmapSlice
import korlibs.io.async.launchImmediately
import korlibs.io.file.VfsFile
import korlibs.korge.render.RenderContext
import korlibs.korge.render.views
import korlibs.korge.time.delayFrame
import korlibs.korge.view.*
import korlibs.korge.view.fast.FSpriteFromIndex
import korlibs.korge.view.fast.FSprites
import korlibs.korge.view.property.ViewProperty
import korlibs.korge.view.property.ViewPropertyFileRef
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.geom.mutable
import korlibs.math.geom.toPoint
import korlibs.time.NIL
import korlibs.time.milliseconds
import kotlinx.coroutines.CancellationException
import kotlin.random.Random
import kotlin.time.Duration

inline fun Container.particleEmitter(
    emitter: ParticleEmitter,
    emitterPos: Point = Point.ZERO,
    time: Duration = Duration.NIL,
    localCoords: Boolean = false,
    random: Random = Random,
    callback: ParticleEmitterView.() -> Unit = {}
) = ParticleEmitterView(emitter, emitterPos, localCoords, random).apply { this.timeUntilStop = time }
    .addTo(this, callback)

suspend fun Container.attachParticleAndWait(particle: ParticleEmitter, x: Double, y: Double, time: Duration = Duration.NIL, speed: Double = 1.0) {
    val p = particle.create(x, y, time)
    p.speed = speed
    this += p
    p.waitComplete()
    this -= p
}

class ParticleEmitterView(private var emitter: ParticleEmitter, emitterPos: Point = Point.ZERO, localCoords: Boolean = false, random: Random = Random) : View(),
    ViewFileRef by ViewFileRef.Mixin() {
    var simulator: ParticleEmitterSimulator = ParticleEmitterSimulator(emitter, emitterPos, random)
    var timeUntilStop by simulator::timeUntilStop
    var emitting by simulator::emitting
    val aliveCount by simulator::aliveCount
    val anyAlive by simulator::anyAlive

    @ViewProperty(min = -1000.0, max = +1000.0)
    var emitterPos: Point
        get() = simulator.emitterPos
        set(value) {
            simulator.emitterPos = value
        }
    var emitterXY: Point
        get() = emitterPos
        set(value) {
            emitterPos = value
        }
    var emitterX: Double
        get() = emitterPos.x
        set(value) {
            emitterPos = emitterPos.copy(x = value)
        }
    var emitterY: Double
        get() = emitterPos.y
        set(value) {
            emitterPos = emitterPos.copy(y = value)
        }

    @ViewProperty
    var localCoords: Boolean = localCoords

    private val lastPosition = globalPos.mutable

    var autoInvalidateRenderer: Boolean = true

    init {
        addUpdater { step(it) }
    }

    fun step(dt: Duration) {
        if (dt > 0.milliseconds) {
            val g = globalPos / stage!!.scaleXY.toPoint()
            val gx = g.x
            val gy = g.y
            val dx = if (this.localCoords) 0.0 else lastPosition.x - gx
            val dy = if (this.localCoords) 0.0 else lastPosition.y - gy
            simulator.simulate(dt, dx, dy)
            lastPosition.setTo(gx, gy)
            if (autoInvalidateRenderer) invalidateRender()
        }
    }

    fun restart() {
        simulator.restart()
    }

    suspend fun waitComplete() {
        while (anyAlive) delayFrame()
    }

    private var cachedBlending = BlendMode.NORMAL

    override fun renderInternal(ctx: RenderContext) {
        if (!visible) return
        run {
            if (!textureLoaded && texture != null) {
                textureLoaded = true
                launchImmediately(ctx.coroutineContext) {
                    forceLoadTexture(ctx.views!!, sourceFile = texture)
                }
            }
            lazyLoadRenderInternal(ctx, this)
        }
        if (cachedBlending.factors.srcRGB != emitter.blendFuncSource || cachedBlending.factors.dstRGB != emitter.blendFuncDestination) {
            cachedBlending = BlendMode(AGBlending(emitter.blendFuncSource, emitter.blendFuncDestination))
        }

        if (fsprites == null || fsprites!!.maxSize < simulator.particles.max) {
            fsprites = FSprites(simulator.particles.max)
        }
        val sprites = fsprites!!
        val tex = emitter.texture ?: Bitmaps.white
        fviewInfo.texs[0] = tex.bmp
        sprites.size = 0
        simulator.particles.forEach { p ->
            if (p.alive()) {
                //if (p.x == 0f && p.y == 0f) println("00: ${p.index}")
                sprites.apply {
                    val fsprite = FSpriteFromIndex(sprites.size++)
                    //println("FSPRITE: ${fsprite.index}")
                    fsprite.setTex(tex)
                    fsprite.colorMul = p.color() * this@ParticleEmitterView.colorMul
                    fsprite.setAnchor(.5f, .5f)
                    fsprite.xy(p.x, p.y)
                    fsprite.scale(p.scale)
                    fsprite.angle = p.rotation
                }
            }
        }
        FSprites.render(
            ctx = ctx,
            sprites = sprites,
            info = fviewInfo,
            smoothing = true,
//            globalMatrix = if (localCoords) globalMatrix else ctx.views!!.stage.localMatrix,
            globalMatrix = globalMatrix,
            blending = cachedBlending
        )
    }

    private var fsprites: FSprites? = null
    private val fviewInfo = FSprites.FViewInfo(1)

    override fun getLocalBoundsInternal() = Rectangle(-30, -30, +30, +30)

    private var textureLoaded: Boolean = false

    @ViewProperty
    @ViewPropertyFileRef(["png", "jpg"])
    var texture: String?
        get() = emitter.textureName
        set(value) {
            textureLoaded = false
            emitter.textureName = value
        }

    suspend fun forceLoadTexture(views: Views, currentVfs: VfsFile = views.currentVfs, sourceFile: String? = null) {
        this.texture = sourceFile
        textureLoaded = true
        try {
            emitter.texture = currentVfs["$sourceFile"].readBitmapSlice()
        } catch (e: Throwable) {
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }

    override suspend fun forceLoadSourceFile(views: Views, currentVfs: VfsFile, sourceFile: String?) {
        baseForceLoadSourceFile(views, currentVfs, sourceFile)
        emitter = currentVfs["$sourceFile"].readParticleEmitter()
        simulator = ParticleEmitterSimulator(emitter, emitterPos)
        scale = 1.0
    }

    @Suppress("unused")
    @ViewProperty
    @ViewPropertyFileRef(["pex"])
    private var pexSourceFile: String? by this::sourceFile

}
