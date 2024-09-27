package korlibs.korge.particle

import korlibs.math.geom.*
import korlibs.time.NIL
import korlibs.time.seconds
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.time.Duration

class ParticleEmitterSimulator(private val emitter: ParticleEmitter, var emitterPos: Point = Point.ZERO, val random: Random = Random) {

    var totalElapsedTime: Duration = 0.seconds
    var timeUntilStop: Duration = Duration.NIL
    var emitting: Boolean = true
    val textureWidth: Int = emitter.texture?.width ?: 16
    val particles: ParticleContainer = ParticleContainer(emitter.maxParticles).apply { init() }
    val aliveCount: Int
        get() {
            var count = 0
            particles.forEach { if (it.alive()) count++ }
            return count
        }
    val anyAlive: Boolean get() = aliveCount > 0

    private fun randomVariance(base: Float, variance: Float): Float = base + variance * (random.nextFloat() * 2f - 1f)
    private fun randomVariance(base: Double, variance: Double): Double = base + variance * (random.nextDouble() * 2.0 - 1.0)
    private fun randomVariance(base: Angle, variance: Angle): Angle = randomVariance(base.degrees, variance.degrees).degrees

    fun ParticleContainer.init() = forEach {
        init(it, true)
    }

    fun ParticleContainer.init(particle: Particle, initialization: Boolean): Particle {
        val lifespan: Float = randomVariance(emitter.lifeSpan, emitter.lifespanVariance).coerceAtLeast(0.001).toFloat()
        particle.totalTime = lifespan.coerceAtLeast(0f)
        if (initialization) {
            val ratio = particle.index.toFloat() / emitter.maxParticles.toFloat()
            particle.currentTime = if (ratio == 0f) 0f else -(emitter.lifeSpan + emitter.lifespanVariance.absoluteValue).toFloat() * ratio
        } else {
            particle.currentTime = 0f
        }
        particle.initializedFloat = 1f
        return particle
    }

    fun ParticleContainer.init2(particle: Particle): Particle {
        val lifespan: Float = particle.totalTime
        val emitterX: Float = emitterPos.x.toFloat()
        val emitterY: Float = emitterPos.y.toFloat()
        particle.x = randomVariance(emitterX, emitter.sourcePositionVariance.x.toFloat())
        particle.y = randomVariance(emitterY, emitter.sourcePositionVariance.y.toFloat())
        particle.startX = emitterX
        particle.startY = emitterY
        val angle = randomVariance(emitter.angle, emitter.angleVariance)
        val speed = randomVariance(emitter.speed, emitter.speedVariance).toFloat()
        particle.velocityX = speed * cosf(angle)
        particle.velocityY = speed * sinf(angle)
        val startRadius = randomVariance(emitter.maxRadius, emitter.maxRadiusVariance).toFloat()
        val endRadius = randomVariance(emitter.minRadius, emitter.minRadiusVariance).toFloat()
        particle.emitRadius = startRadius
        particle.emitRadiusDelta = (endRadius - startRadius) / lifespan
        particle.emitRotation = randomVariance(emitter.angle, emitter.angleVariance)
        particle.emitRotationDelta = randomVariance(emitter.rotatePerSecond, emitter.rotatePerSecondVariance)
        particle.radialAcceleration = randomVariance(emitter.radialAcceleration, emitter.radialAccelVariance).toFloat()
        particle.tangentialAcceleration = randomVariance(emitter.tangentialAcceleration, emitter.tangentialAccelVariance).toFloat()
        val startSize = randomVariance(emitter.startSize, emitter.startSizeVariance).coerceAtLeast(0.1).toFloat()
        val endSize = randomVariance(emitter.endSize, emitter.endSizeVariance).coerceAtLeast(0.1).toFloat()
        particle.scale = startSize / textureWidth
        particle.scaleDelta = ((endSize - startSize) / lifespan) / textureWidth
        particle.colorR = randomVariance(emitter.startColor.r, emitter.startColorVariance.r)
        particle.colorG = randomVariance(emitter.startColor.g, emitter.startColorVariance.g)
        particle.colorB = randomVariance(emitter.startColor.b, emitter.startColorVariance.b)
        particle.colorA = randomVariance(emitter.startColor.a, emitter.startColorVariance.a)
        val endColorR = randomVariance(emitter.endColor.r, emitter.endColorVariance.r)
        val endColorG = randomVariance(emitter.endColor.g, emitter.endColorVariance.g)
        val endColorB = randomVariance(emitter.endColor.b, emitter.endColorVariance.b)
        val endColorA = randomVariance(emitter.endColor.a, emitter.endColorVariance.a)
        particle.colorRdelta = ((endColorR - particle.colorR) / lifespan)
        particle.colorGdelta = ((endColorG - particle.colorG) / lifespan)
        particle.colorBdelta = ((endColorB - particle.colorB) / lifespan)
        particle.colorAdelta = ((endColorA - particle.colorA) / lifespan)
        val startRotation = randomVariance(emitter.rotationStart, emitter.rotationStartVariance)
        val endRotation = randomVariance(emitter.rotationEnd, emitter.rotationEndVariance)
        particle.rotation = startRotation
        particle.rotationDelta = (endRotation - startRotation) / lifespan
        particle.initializedFloat = 1f
        return particle
    }

    fun ParticleContainer.advance(particle: Particle, _elapsedTime: Float, dx: Float = 0.0f, dy: Float = 0.0f) {
        val restTime = particle.totalTime - particle.currentTime
        val elapsedTime = if (restTime > _elapsedTime) _elapsedTime else restTime
        particle.currentTime += elapsedTime
        if (particle.currentTime < 0.0) return
        if ((!particle.initialized() || !particle.alive()) && emitting) {
            init(particle, false)
            init2(particle)
        }
        if (!particle.alive()) return
        when (emitter.emitterType) {
            ParticleEmitter.Type.RADIAL -> {
                particle.emitRotation += particle.emitRotationDelta * elapsedTime.toDouble()
                particle.emitRadius += (particle.emitRadiusDelta * elapsedTime)
                particle.x = emitter.sourcePosition.x.toFloat() - cosf(particle.emitRotation) * particle.emitRadius
                particle.y = emitter.sourcePosition.y.toFloat() - sinf(particle.emitRotation) * particle.emitRadius
            }
            ParticleEmitter.Type.GRAVITY -> {
                val distanceX = particle.x - particle.startX
                val distanceY = particle.y - particle.startY
                val distanceScalar = sqrt(distanceX * distanceX + distanceY * distanceY).coerceAtLeast(0.01f)
                var radialX = distanceX / distanceScalar
                var radialY = distanceY / distanceScalar
                var tangentialX = radialX
                var tangentialY = radialY

                radialX *= particle.radialAcceleration
                radialY *= particle.radialAcceleration

                val newY = tangentialX
                tangentialX = -tangentialY * particle.tangentialAcceleration
                tangentialY = newY * particle.tangentialAcceleration

                particle.velocityX += elapsedTime * (emitter.gravity.x + radialX + tangentialX).toFloat()
                particle.velocityY += elapsedTime * (emitter.gravity.y + radialY + tangentialY).toFloat()
                particle.x += particle.velocityX * elapsedTime
                particle.y += particle.velocityY * elapsedTime
            }
        }
        particle.x += dx
        particle.y += dy
        particle.scale += particle.scaleDelta * elapsedTime
        particle.rotation += particle.rotationDelta * elapsedTime
        particle.colorR += (particle.colorRdelta * elapsedTime)
        particle.colorG += (particle.colorGdelta * elapsedTime)
        particle.colorB += (particle.colorBdelta * elapsedTime)
        particle.colorA += (particle.colorAdelta * elapsedTime)
    }

    fun simulate(time: Duration, dx: Double = 0.0, dy: Double = 0.0) {
        if (emitting) {
            totalElapsedTime += time
            if (timeUntilStop != Duration.NIL && totalElapsedTime >= timeUntilStop) emitting = false
        }
        val timeSeconds = time.seconds.toFloat()
        particles.forEach { p -> advance(p, timeSeconds, dx.toFloat(), dy.toFloat()) }
    }

    fun restart() {
        totalElapsedTime = 0.seconds
        emitting = true
    }

}
