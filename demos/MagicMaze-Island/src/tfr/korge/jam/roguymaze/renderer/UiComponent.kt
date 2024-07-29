package tfr.korge.jam.roguymaze.renderer

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onOut
import com.soywiz.korge.input.onOver
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.degrees
import tfr.korge.jam.roguymaze.*
import tfr.korge.jam.roguymaze.InputEvent.Action
import tfr.korge.jam.roguymaze.lib.EventBus
import tfr.korge.jam.roguymaze.lib.Resources
import tfr.korge.jam.roguymaze.model.World

class UiComponent(val world: World, val res: Resources, val rootView: Stage, val bus: EventBus) {

    companion object {
        val log = Logger("UiComponent")

        suspend operator fun invoke(injector: AsyncInjector) {
            injector.mapSingleton {
                UiComponent(get(), get(), get(), get())
            }
            injector.get<UiComponent>()
        }
    }

    val playerControls = mutableListOf<PlayerControl>()

    init {
        rootView.apply {
            image(res.uiPanelTopLeft) {
                image(res.uiMap) {
                    position(20, 7)
                }
            }
            addRoomCounter()
            image(res.uiPanelTopRight) {
                alignRightToRightOf(rootView)
                image(res.uiTimer) {
                    position(31, 7)
                }
            }
            image(res.uiPanelBottomLeft) {
                alignBottomToBottomOf(rootView)
            }

        }
        addPlayerControls()

        addMovePlayer()

        addMoveMap()

        addZoomMap()

        addRightMenuButtons()
    }

    private fun addRightMenuButtons() = rootView.apply {
        val settingsButton = image(res.buttonSettings) {
            alignRightToRightOf(rootView, 8.0)
            alignTopToTopOf(rootView, 120.0)
            onClick {
                bus.send(OpenSettingsEvent)
            }
        }
        image(res.buttonInfo) {
            alignRightToRightOf(settingsButton)
            alignTopToTopOf(settingsButton, 90.0)
            onClick {
                bus.send(OpenFaqEvent)
            }
        }
    }

    private fun addRoomCounter() = rootView.apply {
        text("1/12", font = res.fontBubble, textSize = 38.0) {
            bus.register<InputEvent> {
                if (it.action == Action.FoundNextRoom) {
                    text = "${world.rooms.size}/${world.totalRooms}"
                }
            }

            position(80.0, 16.0)
        }
    }

    private fun addPlayerControls() = rootView.apply {
        for (playerNumber in 1..4) {
            val playerControl = PlayerControl(playerNumber, playerControls, rootView, res, bus)
            addChild(playerControl)
            playerControls.add(playerControl)
            if (playerNumber == world.selectedHero) {
                playerControl.checkPlayer.select()
            }
        }
    }

    class PlayerControl(val playerNumber: Int,
            val others: MutableList<PlayerControl>,
            val rootView: View,
            res: Resources,
            bus: EventBus) : Container() {

        val checkPlayer: CheckBox

        init {
            alignLeftToLeftOf(rootView, 48.0)
            alignBottomToBottomOf(rootView, 48.0)
            val distance = (playerNumber - 1) * 65.0

            checkPlayer = CheckBox(res.uiCheckDisabled, res.uiCheckEnabled, {
                bus.send(InputEvent(Action.SelectHero, playerNumber))
            }) {
                anchor(0.5, 0.0)
                x = distance
            }
            addChild(checkPlayer)
            val player = image(res.getUiPlayer(playerNumber)) {
                anchor(0.5, 0.0)
                alignLeftToLeftOf(checkPlayer)
                alignBottomToTopOf(checkPlayer, 12.0)
                x = distance
            }
            val maskBase = image(res.uiMaskEmpty) {
                anchor(0.5, 0.0)
                alignLeftToLeftOf(player)
                alignBottomToTopOf(player, 8.0)
                x = distance
            }

            val mask = image(res.getUiMask(playerNumber)) {
                anchor(0.5, 0.0)
                alignLeftToLeftOf(player)
                alignBottomToTopOf(player, 12.0)
                x = distance
                alpha = 0.0
            }

            val homeBase = image(res.uiHomeEmpty) {
                anchor(0.5, 0.0)
                alignLeftToLeftOf(maskBase)
                alignBottomToTopOf(maskBase, 14.0)
                x = distance
            }

            val home = image(res.getUiHome(playerNumber)) {
                anchor(0.5, 0.0)
                alignLeftToLeftOf(maskBase)
                alignBottomToTopOf(maskBase, 18.0)
                x = distance
                alpha = 0.0
            }

            bus.register<FoundMaskEvent> {
                if (it.playerNumber == playerNumber) {
                    mask.alpha = 1.0
                }
            }
            bus.register<FoundHomeEvent> {
                if (it.playerNumber == playerNumber) {
                    home.alpha = 1.0
                    checkPlayer.uncheck()
                    checkPlayer.enabled = false
                }
            }

            bus.register<InputEvent> {
                if (it.action == Action.SelectHero) {
                    if (it.heroNumber == playerNumber) {
                        checkPlayer.select()
                    } else {
                        checkPlayer.uncheck()
                    }
                }
            }


        }

    }


    class CheckBox(val base: BmpSlice,
            val checked: BmpSlice,
            action: (CheckBox) -> Unit,
            adjust: Image.() -> Unit = {}) : Container() {
        var checkedState = false
        var enabled = true

        val checkedImage: Image

        fun select() {
            checkedState = true
            checkedImage.alpha = 1.0
        }

        fun uncheck() {
            checkedState = false
            checkedImage.alpha = 0.0
        }

        init {
            image(base) {
                adjust.invoke(this)
            }
            checkedImage = image(checked) {
                adjust.invoke(this)

                alpha = 0.0

                onClick {
                    if (enabled) {

                        action.invoke(this@CheckBox)
                        select()
                    }
                }

                onOver {
                    if (enabled) {
                        alpha = 1.0
                    }
                }
                onOut {
                    if (!checkedState && enabled) {
                        alpha = 0.0
                    }
                }
            }

        }

    }

    private fun addZoomMap() = rootView.apply {
        val zoomOut = image(res.uiMapZoomOut) {
            anchor(0.5, 1.0)
            alignBottomToBottomOf(rootView, 150.0)
            alignRightToRightOf(rootView, 60.0)
            onClick { sendUiEvent(Action.MapZoomOut) }
        }

        val zoomIn = image(res.uiMapZoomIn) {
            anchor(0.5, 1.0)
            alignLeftToLeftOf(zoomOut)
            alignBottomToTopOf(zoomOut, 10.0)
            onClick { sendUiEvent(Action.MapZoomIn) }
        }
    }

    private fun addMovePlayer() = rootView.apply {
        val distance = 0.3
        val movePlayerDown = image(res.uiActionMoveDown) {
            anchor(0.5, distance)
            alignLeftToLeftOf(rootView, 390.0)
            alignBottomToBottomOf(rootView, 30.0)
            onClick { sendUiEvent(Action.HeroDown) }
            anchor(0.5, -distance)
        }
        val movePlayerLeft = image(res.uiActionMoveDown) {
            position(movePlayerDown.pos)
            anchor(0.5, -distance)
            rotation(90.degrees)
            onClick { sendUiEvent(Action.HeroLeft) }
        }
        val movePlayerUp = image(res.uiActionMoveDown) {
            position(movePlayerDown.pos)
            anchor(0.5, -distance)
            rotation(180.degrees)
            onClick { sendUiEvent(Action.HeroUp) }
        }
        val movePlayerRight = image(res.uiActionMoveDown) {
            position(movePlayerDown.pos)
            anchor(0.5, -distance)
            rotation(270.degrees)
            onClick { sendUiEvent(Action.HeroRight) }
        }
        val actionSearch = image(res.uiActionSearch) {
            alignLeftToRightOf(movePlayerUp, 50.0)
            alignBottomToTopOf(movePlayerRight, -35.0)

            onClick { sendUiEvent(Action.ActionSearch) }
        }

        fun updateActions() {
            val newActionSet = world.getAllowedActions()
            movePlayerDown.visible = newActionSet.contains(Action.HeroDown)
            movePlayerLeft.visible = newActionSet.contains(Action.HeroLeft)
            movePlayerRight.visible = newActionSet.contains(Action.HeroRight)
            movePlayerUp.visible = newActionSet.contains(Action.HeroUp)
            actionSearch.visible = newActionSet.contains(Action.ActionSearch)
        }

        bus.register<ChangePlayerEvent> {
            updateActions()
        }
        bus.register<ChangePlayersCountEvent> {
            updateActions()
        }
    }

    private fun addMoveMap() = rootView.apply {
        val distance = 0.5
        val moveMap = image(res.uiMapMoveDown) {
            anchor(0.5, distance)
            alignRightToRightOf(rootView, 64.0)
            alignBottomToBottomOf(rootView, 64.0)
            onClick { sendUiEvent(Action.MapMoveDown) }
            anchor(0.5, -distance)
        }
        image(res.uiMapMoveDown) {
            position(moveMap.pos)
            anchor(0.5, -distance)
            rotation(90.degrees)
            onClick { sendUiEvent(Action.MapMoveLeft) }
        }
        image(res.uiMapMoveDown) {
            position(moveMap.pos)
            anchor(0.5, -distance)
            rotation(180.degrees)
            onClick { sendUiEvent(Action.MapMoveUp) }
        }
        image(res.uiMapMoveDown) {
            position(moveMap.pos)
            anchor(0.5, -distance)
            rotation(270.degrees)
            onClick { sendUiEvent(Action.MapMoveRight) }
        }
    }


    fun sendUiEvent(action: Action) {
        log.info { "New UI input Event $action" }
        bus.send(InputEvent(action, world.selectedHero))
    }


}