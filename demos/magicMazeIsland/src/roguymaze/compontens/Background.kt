package tfr.korge.jam.roguymaze.compontens

import korlibs.inject.Injector
import korlibs.korge.view.Stage
import korlibs.korge.view.image
import tfr.korge.jam.roguymaze.lib.Resources
import tfr.korge.jam.roguymaze.renderer.UiComponent

class Background(override val view: Stage, res: Resources) : UiComponent {

    companion object {
        suspend operator fun invoke(injector: Injector): Background {
            injector.run {
                return Background(get(), get())
            }
        }
    }

    init {
        view.image(res.imageBackground)
    }

}