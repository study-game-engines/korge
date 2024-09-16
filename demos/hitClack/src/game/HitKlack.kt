package de.tfr.game


import de.tfr.game.audio.SoundMachine
import de.tfr.game.lib.actor.Box2D
import de.tfr.game.model.GameField
import de.tfr.game.renderer.DisplayRenderer
import de.tfr.game.renderer.GameFieldRenderer
import de.tfr.game.renderer.LogoRenderer
import korlibs.korge.view.Container
import korlibs.korge.view.Stage
import korlibs.time.seconds
import resolution
import kotlin.time.Duration

class HitKlack(override val view: Stage) : UpdateComponent {

    private lateinit var renderer: GameFieldRenderer
    private lateinit var controller: Controller
    private lateinit var display: Display
    private lateinit var displayRenderer: DisplayRenderer
    private lateinit var game: BoxGame
    private lateinit var logo: LogoRenderer

    private val gameField = GameField(10)

    suspend fun initGame(container: Container) = apply {
        val center = resolution.getCenter()
        renderer = GameFieldRenderer(center, gameField).init(container)
        val soundMachine = SoundMachine(view).init()
        game = BoxGame(gameField, sounds = soundMachine)

        val gameFieldSize = renderer.getFieldSize(gameField.size)
        controller = Controller(center, gameFieldSize, view).init(container)
        container.addComponent(controller)
        controller.addTouchListener(game)
        display = Display(Box2D(center, 280.0, 90.0))
        displayRenderer = DisplayRenderer(display).init(container)
        logo = LogoRenderer(center, gameFieldSize).init(container)
    }

    override fun update(dt: Duration) {
        val deltaTime = dt.seconds
        game.update(deltaTime)
        displayRenderer.render()
    }

}