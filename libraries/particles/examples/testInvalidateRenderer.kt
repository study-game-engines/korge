package korge.particles.examples

import korlibs.graphics.log.AGBaseLog
import korlibs.io.async.noSuspend
import korlibs.io.file.std.SingleFileMemoryVfs
import korlibs.korge.particle.ParticleEmitter
import korlibs.korge.particle.ParticleEmitterView
import korlibs.korge.particle.readParticleEmitter
import korlibs.korge.tests.ViewsForTesting
import korlibs.korge.view.ViewsLog
import korlibs.korge.view.addTo
import korlibs.time.seconds
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals

fun testInvalidateRenderer() {
    val viewsLog: ViewsLog = ViewsLog(EmptyCoroutineContext)
    val view: ParticleEmitterView = ParticleEmitterView(particleEmitter).addTo(viewsLog.stage)
    viewsLog.gameWindow.updatedSinceFrame = 0
    view.step(10.seconds)
    assertEquals(1, viewsLog.gameWindow.updatedSinceFrame)
    view.step(10.seconds)
    assertEquals(2, viewsLog.gameWindow.updatedSinceFrame)
    viewsLog.gameWindow.updatedSinceFrame = 0
    view.autoInvalidateRenderer = false
    view.step(10.seconds)
    assertEquals(0, viewsLog.gameWindow.updatedSinceFrame)
}

val particleEmitter: ParticleEmitter = SingleFileMemoryVfs("""
<particleEmitterConfig>
    <texture name="texture.png"/>
    <sourcePosition x="300.00" y="300.00"/>
    <sourcePositionVariance x="0.00" y="0.00"/>
    <speed value="100.00"/>
    <speedVariance value="30.00"/>
    <particleLifeSpan value="2.0000"/>
    <particleLifespanVariance value="1.9000"/>
    <angle value="270.00"/>
    <angleVariance value="2.00"/>
    <gravity x="0.00" y="0.00"/>
    <radialAcceleration value="0.00"/>
    <tangentialAcceleration value="0.00"/>
    <radialAccelVariance value="0.00"/>
    <tangentialAccelVariance value="0.00"/>
    <startColor red="1.00" green="0.31" blue="0.00" alpha="0.62"/>
    <startColorVariance red="0.00" green="0.00" blue="0.00" alpha="0.00"/>
    <finishColor red="1.00" green="0.31" blue="0.00" alpha="0.00"/>
    <finishColorVariance red="0.00" green="0.00" blue="0.00" alpha="0.00"/>
    <maxParticles value="500"/>
    <startParticleSize value="70.00"/>
    <startParticleSizeVariance value="49.53"/>
    <finishParticleSize value="10.00"/>
    <FinishParticleSizeVariance value="5.00"/>
    <duration value="-1.00"/>
    <emitterType value="0"/>
    <maxRadius value="100.00"/>
    <maxRadiusVariance value="0.00"/>
    <minRadius value="0.00"/>
    <minRadiusVariance value="0.00"/>
    <rotatePerSecond value="0.00"/>
    <rotatePerSecondVariance value="0.00"/>
    <blendFuncSource value="770"/>
    <blendFuncDestination value="1"/>
    <rotationStart value="0.00"/>
    <rotationStartVariance value="0.00"/>
    <rotationEnd value="0.00"/>
    <rotationEndVariance value="0.00"/>
</particleEmitterConfig>
""".trimIndent()
).noSuspend { readParticleEmitter() }

class ParticleEmitterViewTest : ViewsForTesting(log = true) {
    override fun filterLogDraw(str: String, kind: AGBaseLog.Kind): Boolean = kind == AGBaseLog.Kind.DRAW
    //@Test
    //fun test() = korgeOffscreenTest(512, 512) {
    //    val emitter = particleEmitter(particleEmitter, random = Random(0L)
    //    ) {
    //        xy(100, 100)
    //    }
    //    emitter.updateSingleView(1.seconds)
    //    assertScreenshot(this, "particles", includeBackground = true)
    //    /*
    //    for (n in 0 until 20) {
    //        delayFrame()
    //        emitter.xy(120 + n * 5, 100)
    //    }
    //    */
    //}
}
