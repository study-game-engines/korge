package tfr.korge.jam.roguymaze.renderer

import korlibs.inject.Injector
import korlibs.korge.input.onClick
import korlibs.korge.view.Container
import korlibs.korge.view.Stage
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.logger.Logger
import tfr.korge.jam.roguymaze.OpenFaqEvent
import tfr.korge.jam.roguymaze.lib.EventBus
import tfr.korge.jam.roguymaze.lib.Resources
import tfr.korge.jam.roguymaze.model.World

class FaqComponent(val world: World, val rootView: Stage, res: Resources, val bus: EventBus) : Container() {

    companion object {
        val log = Logger("FaqComponent")

        suspend operator fun invoke(injector: Injector): FaqComponent {
            injector.mapSingleton {
                FaqComponent(get(), get(), get(), get())
            }
            return injector.get()
        }
    }


    init {
        val page1 = image(res.helpPage1) {
            //centerOn(rootView)
            position(120, 50)
            visible = false
        }
        val page2 = image(res.helpPage2) {
            //centerOn(rootView)
            position(120, 50)
            visible = false
        }
        val page3 = image(res.helpPage3) {
            //centerOn(rootView)
            position(120, 50)
            visible = false
        }
        page1.onClick {
            page1.visible = false
            page2.visible = true
        }
        page2.onClick {
            page2.visible = false
            page3.visible = true
        }
        page3.onClick {
            this@FaqComponent.visible = false
        }

        bus.register<OpenFaqEvent> {
            if (!visible) {
                visible = true
                page1.visible = true
                page2.visible = false
                page3.visible = false
            } else {
                visible = false
            }
        }

        visible = false


    }


}