package tfr.korge.jam.roguymaze.renderer

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korinject.AsyncInjector
import tfr.korge.jam.roguymaze.OpenFaqEvent
import tfr.korge.jam.roguymaze.lib.EventBus
import tfr.korge.jam.roguymaze.lib.Resources
import tfr.korge.jam.roguymaze.model.World

class FaqComponent(val world: World, val rootView: Stage, res: Resources, val bus: EventBus) : Container() {

    companion object {
        val log = Logger("FaqComponent")

        suspend operator fun invoke(injector: AsyncInjector): FaqComponent {
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