package j4k.candycrush

import j4k.candycrush.lib.*
import j4k.candycrush.model.*
import j4k.candycrush.renderer.*
import korlibs.event.Key
import korlibs.inject.Injector
import korlibs.inject.InjectorDependency
import korlibs.korge.view.Stage
import korlibs.logger.Logger

/**
 * Global keyboard bindings.
 */
class KeyBindings(
    private val stage: Stage,
    private val bus: EventBus,
    private val level: Level,
    private val fieldRenderer: GameFieldRenderer
) : InjectorDependency {

    companion object {
        val log = Logger<KeyBindings>()

        suspend operator fun invoke(injector: Injector): KeyBindings {
            injector.mapSingleton {
                KeyBindings(get(), get(), get(), get())
            }
            return injector.get()
        }
    }

    override fun init(injector: Injector) {
        bindKeys()
    }

    private fun bindKeys() {
        stage.keys {
            down {
                onKeyDown(it.key)
            }
        }
    }

    private fun onKeyDown(key: Key) {
        when (key) {
            Key.P -> {
                log.debug { "Print Field Data" }
                println(level.field)
            }
            Key.D -> {
                log.debug { "Show Debug Letters" }
                fieldRenderer.toggleDebug()
            }
            Key.S -> {
                bus.send(ShuffleGameEvent)
            }
            Key.R -> {
                bus.send(ResetGameEvent)
            }
            Key.I -> {
                log.debug { "Print Image Data" }
                println(fieldRenderer)
                println("Renderer data is equal to field data: " + fieldRenderer.isEqualWithField())
            }
            else -> {
                log.debug { "Pressed unmapped key: $key" }
            }
        }
    }

}
