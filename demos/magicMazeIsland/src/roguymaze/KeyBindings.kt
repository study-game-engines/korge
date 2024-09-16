package tfr.korge.jam.roguymaze

import korlibs.event.Key
import korlibs.inject.Injector
import korlibs.inject.InjectorDependency
import korlibs.korge.view.Stage
import korlibs.logger.Logger
import tfr.korge.jam.roguymaze.InputEvent.Action
import tfr.korge.jam.roguymaze.lib.EventBus
import tfr.korge.jam.roguymaze.model.Room
import tfr.korge.jam.roguymaze.model.World

class KeyBindings(private val stage: Stage,
        private val bus: EventBus,
        private val world: World,
        private val gameFlow: GameFlow,
        private val levelCheck: LevelCheck,
        private val room: Room) : InjectorDependency {


    companion object {
        val log = Logger("KeyBindings")

        suspend operator fun invoke(injector: Injector): KeyBindings {
            injector.mapSingleton {
                KeyBindings(get(), get(), get(), get(), get(), get())
            }
            return injector.get()
        }
    }

    override fun init(injector: Injector) {
        bindKeys()
        bus.register<ResetGameEvent> { reloadLevel() }
    }

    private fun bindKeys() {
        stage.onKeyDown {
            onKeyDown(it.key)
        }
    }

    private fun resetState() {
        gameFlow.reset()
        levelCheck.reset()
    }

    private fun reloadLevel() {
        log.debug { "Reload level" }
        resetState()
        room.reset()
    }

    fun sendPlayerInputEvent(action: Action) {
        bus.send(InputEvent(action, world.selectedHero))
    }

    fun Action.isAllowed() = world.getAllowedActions().contains(this)

    private fun onKeyDown(key: Key) {
        when (key) {
            Key.W -> {
                if (Action.HeroUp.isAllowed()) {
                    sendPlayerInputEvent(Action.HeroUp)
                }
            }
            Key.A -> {
                if (Action.HeroLeft.isAllowed()) {
                    sendPlayerInputEvent(Action.HeroLeft)
                }
            }
            Key.S -> {
                if (Action.HeroDown.isAllowed()) {
                    sendPlayerInputEvent(Action.HeroDown)
                }
            }
            Key.D -> {
                if (Action.HeroRight.isAllowed()) {
                    sendPlayerInputEvent(Action.HeroRight)
                }
            }
            Key.SPACE -> {
                if (Action.ActionSearch.isAllowed()) {
                    gameFlow.findNewRoom()
                }
            }
            Key.PLUS, Key.KP_ADD -> {
                sendPlayerInputEvent(Action.MapZoomIn)
            }
            Key.MINUS, Key.KP_SUBTRACT -> {
                sendPlayerInputEvent(Action.MapZoomOut)
            }
            Key.P -> {
                log.debug { "Print Field Data" }
                println(room.ground)
            }

            Key.LEFT -> {
                sendPlayerInputEvent(Action.MapMoveLeft)
            }
            Key.RIGHT -> {
                sendPlayerInputEvent(Action.MapMoveRight)
            }
            Key.UP -> {
                sendPlayerInputEvent(Action.MapMoveUp)
            }
            Key.DOWN -> {
                sendPlayerInputEvent(Action.MapMoveDown)
            }

            Key.N1 -> {
                bus.send(InputEvent(Action.SelectHero, heroNumber = 1))
            }
            Key.N2 -> {
                bus.send(InputEvent(Action.SelectHero, heroNumber = 2))
            }
            Key.N3 -> {
                bus.send(InputEvent(Action.SelectHero, heroNumber = 3))
            }
            Key.N4 -> {
                bus.send(InputEvent(Action.SelectHero, heroNumber = 4))
            }

            else -> {
                log.debug { "Pressed unmapped key: $key" }
            }
        }
    }


}