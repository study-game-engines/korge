package korlibs.korge.input

import korlibs.datastructure.Extra
import korlibs.datastructure.iterators.fastForEach
import korlibs.event.*
import korlibs.io.async.Signal
import korlibs.io.async.launchImmediately
import korlibs.korge.view.View
import korlibs.korge.view.Views
import korlibs.math.geom.Point
import kotlin.jvm.JvmOverloads

class GamePadEvents(val view: View) {

    @PublishedApi
    internal lateinit var views: Views

    @PublishedApi
    internal val coroutineContext get() = views.coroutineContext

    val gamepads: GamePadUpdateEvent = GamePadUpdateEvent()
    val updated: Signal<GamePadUpdateEvent> = Signal()
    val updatedGamepad: Signal<GamepadInfo> = Signal()
    val stick: Signal<GamePadStickEvent> = Signal()
    val button: Signal<GamePadButtonEvent> = Signal()
    val connection: Signal<GamePadConnectionEvent> = Signal()

    fun stick(callback: suspend (playerId: Int, stick: GameStick, x: Double, y: Double) -> Unit) {
        stick { event ->
            launchImmediately(coroutineContext) {
                callback(event.gamepad, event.stick, event.x, event.y)
            }
        }
    }

    fun button(callback: suspend (playerId: Int, pressed: Boolean, button: GameButton, value: Float) -> Unit) {
        button { event ->
            launchImmediately(coroutineContext) {
                callback(event.gamepad, event.type == GamePadButtonEvent.Type.DOWN, event.button, event.value)
            }
        }
    }

    fun button(playerId: Int, callback: suspend (pressed: Boolean, button: GameButton, value: Float) -> Unit) {
        button { event ->
            if (event.gamepad == playerId) {
                launchImmediately(coroutineContext) {
                    callback(event.type == GamePadButtonEvent.Type.DOWN, event.button, event.value)
                }
            }
        }
    }

    fun down(playerId: Int, button: GameButton, callback: suspend () -> Unit) {
        button { event ->
            if (event.gamepad == playerId && event.button == button && event.type == GamePadButtonEvent.Type.DOWN) {
                launchImmediately(coroutineContext) { callback() }
            }
        }
    }

    fun up(playerId: Int, button: GameButton, callback: suspend () -> Unit) {
        button { event ->
            if (event.gamepad == playerId && event.button == button && event.type == GamePadButtonEvent.Type.UP) {
                launchImmediately(coroutineContext) { callback() }
            }
        }
    }

    fun updatedGamepad(callback: (GamepadInfo) -> Unit) {
        this.updatedGamepad.add(callback)
    }

    fun connected(callback: suspend (playerId: Int) -> Unit) {
        connection { e ->
            if (e.type == GamePadConnectionEvent.Type.CONNECTED) {
                launchImmediately(coroutineContext) { callback(e.gamepad) }
            }
        }
    }

    fun disconnected(callback: suspend (playerId: Int) -> Unit) {
        connection { e ->
            if (e.type == GamePadConnectionEvent.Type.DISCONNECTED) {
                launchImmediately(coroutineContext) { callback(e.gamepad) }
            }
        }
    }

    private val oldGamepads = GamePadUpdateEvent()
    private val stickEvent = GamePadStickEvent()
    private val buttonEvent = GamePadButtonEvent()

    init {
        view.onEvent(GamePadUpdateEvent) { event ->
            views = event.target as Views
            gamepads.copyFrom(event)
            var gamepadsUpdated = false
            for (gamepadIndex in 0 until event.gamepadsLength) {
                val gamepad = event.gamepads[gamepadIndex]
                val oldGamepad = this.oldGamepads.gamepads[gamepadIndex]
                var updateCount = 0
                GameButton.BUTTONS.fastForEach { button ->
                    if (gamepad[button] != oldGamepad[button]) {
                        updateCount++
                        button(buttonEvent.apply {
                            this.gamepad = gamepad.index
                            this.type = if (gamepad[button] != 0f) GamePadButtonEvent.Type.DOWN else GamePadButtonEvent.Type.UP
                            this.button = button
                            this.value = gamepad[button]
                        })
                    }
                }
                GameStick.STICKS.fastForEach { stick ->
                    val vector = gamepad[stick]
                    if (vector != oldGamepad[stick]) {
                        updateCount++
                        stick(stickEvent.apply {
                            this.gamepad = gamepad.index
                            this.stick = stick
                            this.pos = vector
                        })
                    }
                }
                if (updateCount > 0) {
                    updatedGamepad(gamepad)
                    gamepadsUpdated = true
                }
            }
            oldGamepads.copyFrom(event)
            if (gamepadsUpdated) updated(event)
        }
        view.onEvents(*GamePadConnectionEvent.Type.ALL) { event ->
            this.views = event.target as Views
            connection(event)
        }
    }
}

data class GamePadStickEvent(var gamepad: Int = 0, var stick: GameStick = GameStick.LEFT, var pos: Point = Point.ZERO) : TypedEvent<GamePadStickEvent>(GamePadStickEvent) {
    val x: Double get() = pos.x
    val y: Double get() = pos.y

    companion object : EventType<GamePadStickEvent>

    fun copyFrom(original: GamePadStickEvent) {
        this.gamepad = original.gamepad
        this.stick = original.stick
        this.pos = original.pos
    }
}

data class GamePadButtonEvent @JvmOverloads constructor(
    override var type: Type = Type.DOWN,
    var gamepad: Int = 0,
    var button: GameButton = GameButton.BUTTON_SOUTH,
    var value: Float = 0f
) : Event(), TEvent<GamePadButtonEvent> {
    enum class Type : EventType<GamePadButtonEvent> { UP, DOWN }

    fun copyFrom(other: GamePadButtonEvent) {
        this.type = other.type
        this.gamepad = other.gamepad
        this.button = other.button
        this.value = other.value
    }
}

val View.gamepad: GamePadEvents by Extra.PropertyThis { GamePadEvents(this) }
inline fun <T> View.gamepad(callback: GamePadEvents.() -> T): T = gamepad.run(callback)
