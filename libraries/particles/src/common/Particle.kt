package korlibs.korge.particle

import korlibs.image.color.RGBA
import korlibs.io.util.toStringDecimal
import korlibs.math.geom.Angle
import korlibs.math.geom.radians
import korlibs.memory.Buffer
import korlibs.memory.Float32Buffer
import korlibs.memory.allocDirect
import korlibs.memory.f32

private const val PARTICLE_STRIDE: Int = 27

class Particle(val index: Int) {
    val offset: Int = index * PARTICLE_STRIDE
}

open class ParticleContainer(val max: Int) {

    private val data: Float32Buffer = Buffer.allocDirect(max * PARTICLE_STRIDE * Float.SIZE_BYTES).f32

    var Particle.rotation: Angle
        get() = rotationRadians.radians
        set(value) {
            rotationRadians = value.radians.toFloat()
        }
    var Particle.x: Float
        get() = data[offset + 0]
        set(value) {
            data[offset + 0] = value
        }
    var Particle.y: Float
        get() = data[offset + 1]
        set(value) {
            data[offset + 1] = value
        }
    var Particle.scale: Float
        get() = data[offset + 2]
        set(value) {
            data[offset + 2] = value
        }
    var Particle.rotationRadians: Float
        get() = data[offset + 3]
        set(value) {
            data[offset + 3] = value
        }
    var Particle.currentTime: Float
        get() = data[offset + 4]
        set(value) {
            data[offset + 4] = value
        }
    var Particle.totalTime: Float
        get() = data[offset + 5]
        set(value) {
            data[offset + 5] = value
        }
    var Particle.colorR: Float
        get() = data[offset + 6]
        set(value) {
            data[offset + 6] = value
        }
    var Particle.colorG: Float
        get() = data[offset + 7]
        set(value) {
            data[offset + 7] = value
        }
    var Particle.colorB: Float
        get() = data[offset + 8]
        set(value) {
            data[offset + 8] = value
        }
    var Particle.colorA: Float
        get() = data[offset + 9]
        set(value) {
            data[offset + 9] = value
        }
    var Particle.colorRdelta: Float
        get() = data[offset + 10]
        set(value) {
            data[offset + 10] = value
        }
    var Particle.colorGdelta: Float
        get() = data[offset + 11]
        set(value) {
            data[offset + 11] = value
        }
    var Particle.colorBdelta: Float
        get() = data[offset + 12]
        set(value) {
            data[offset + 12] = value
        }
    var Particle.colorAdelta: Float
        get() = data[offset + 13]
        set(value) {
            data[offset + 13] = value
        }
    var Particle.startX: Float
        get() = data[offset + 14]
        set(value) {
            data[offset + 14] = value
        }
    var Particle.startY: Float
        get() = data[offset + 15]
        set(value) {
            data[offset + 15] = value
        }
    var Particle.velocityX: Float
        get() = data[offset + 16]
        set(value) {
            data[offset + 16] = value
        }
    var Particle.velocityY: Float
        get() = data[offset + 17]
        set(value) {
            data[offset + 17] = value
        }
    var Particle.radialAcceleration: Float
        get() = data[offset + 18]
        set(value) {
            data[offset + 18] = value
        }
    var Particle.tangentialAcceleration: Float
        get() = data[offset + 19]
        set(value) {
            data[offset + 19] = value
        }
    var Particle.emitRadius: Float
        get() = data[offset + 20]
        set(value) {
            data[offset + 20] = value
        }
    var Particle.emitRadiusDelta: Float
        get() = data[offset + 21]
        set(value) {
            data[offset + 21] = value
        }
    var Particle.scaleDelta: Float
        get() = data[offset + 22]
        set(value) {
            data[offset + 22] = value
        }
    var Particle.emitRotationRadians: Float
        get() = data[offset + 23]
        set(value) {
            data[offset + 23] = value
        }
    var Particle.emitRotationDeltaRadians: Float
        get() = data[offset + 24]
        set(value) {
            data[offset + 24] = value
        }
    var Particle.rotationDeltaRadians: Float
        get() = data[offset + 25]
        set(value) {
            data[offset + 25] = value
        }
    var Particle.initializedFloat: Float
        get() = data[offset + 26]
        set(value) {
            data[offset + 26] = value
        }
    var Particle.emitRotation: Angle
        get() = emitRotationRadians.radians
        set(value) {
            emitRotationRadians = value.radians.toFloat()
        }
    var Particle.emitRotationDelta: Angle
        get() = emitRotationDeltaRadians.radians
        set(value) {
            emitRotationDeltaRadians = value.radians.toFloat()
        }
    var Particle.rotationDelta: Angle
        get() = rotationDeltaRadians.radians
        set(value) {
            rotationDeltaRadians = value.radians.toFloat()
        }

    init {
        for (index in 0 until max) {
            val particle: Particle = Particle(index)
            particle.scale = 1f
            particle.colorR = 1f
            particle.colorG = 1f
            particle.colorB = 1f
            particle.colorA = 1f
        }
    }

    fun Particle.initialized(): Boolean = initializedFloat != 0f
    fun Particle.color(): RGBA = RGBA.float(colorR, colorG, colorB, colorA)
    fun Particle.alive(): Boolean = this.currentTime >= 0.0 && this.currentTime < this.totalTime

    override fun toString(): String {
        fun RGBA.nice(): String = toString()
        fun Float.nice(): String = toStringDecimal(1)
        fun Double.nice(): String = toStringDecimal(1)
        fun Angle.nice(): String = degrees.toStringDecimal(1)
        fun Particle.toStringDefault(): String =
            "Particle[$index](initialized=${initialized()},pos=(${x.nice()},${y.nice()}),start=(${startX.nice()},${startY.nice()}),velocity=(${velocityX.nice()},${velocityY.nice()}),scale=${scale.nice()},rotation=${rotation},time=${currentTime.nice()}/${totalTime.nice()},color=${color().nice()},colorDelta=${colorRdelta.nice()},${colorGdelta.nice()},${colorBdelta.nice()},${colorAdelta.nice()}),radialAcceleration=${radialAcceleration.nice()},tangentialAcceleration=${tangentialAcceleration.nice()},emitRadius=${emitRadius.nice()},emitRadiusDelta=${emitRadiusDelta.nice()},scaleDelta=${scaleDelta.nice()},emitRotation=${emitRotation.nice()},emitRotationDelta=${emitRotationDelta.nice()}"
        return "ParticleContainer[$max](\n${map { if (it.initialized()) it.toStringDefault() else null }.filterNotNull().joinToString("\n")}\n)"
    }

}

inline fun <T : ParticleContainer> T.forEach(action: T.(Particle) -> Unit) {
    for (index in 0 until max) {
        action(Particle(index))
    }
}

inline fun <T : ParticleContainer, R> T.map(action: T.(Particle) -> R): List<R> {
    return (0 until max).map { action(Particle(it)) }
}
