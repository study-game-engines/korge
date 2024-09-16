package j4k.candycrush.compontens

import j4k.candycrush.lib.*
import korlibs.inject.Injector
import korlibs.korge.view.Container
import korlibs.korge.view.Image

class Background(res: Resources) : Container() {

    companion object {
        suspend operator fun invoke(injector: Injector): Background {
            injector.run {
                return Background(get())
            }
        }
    }

    init {
        addChild(Image(res.imageBackground))
    }

}
