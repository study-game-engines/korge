import korlibs.korge.*
import korlibs.korge.scene.*

suspend fun main() = Korge().start {
    sceneContainer().changeTo({ MainParticles() })
}
