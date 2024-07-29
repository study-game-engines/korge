package korlibs.korge.particles

import korlibs.korge.*
import korlibs.korge.particle.ParticleEmitter
import korlibs.korge.particle.ParticleEmitterView
import korlibs.korge.view.ViewFactory
import korlibs.korge.view.Views

class ParticlesViewsCompleter : ViewsCompleter {
    override fun completeViews(views: Views) {
        views.viewFactories.addAll(listOf(
            ViewFactory("ParticleEmitter") { ParticleEmitterView(ParticleEmitter()) },
        ))
    }
}
