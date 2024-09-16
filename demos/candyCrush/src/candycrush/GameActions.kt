package j4k.candycrush

import j4k.candycrush.lib.*
import j4k.candycrush.model.*
import j4k.candycrush.renderer.*
import j4k.candycrush.renderer.animation.*
import korlibs.inject.Injector
import korlibs.inject.InjectorDependency
import korlibs.logger.Logger

/**
 * Provides global game actions like [shuffle] or [reloadLevel].
 * Action can be called with event bus events [ResetGameEvent] and [ShuffleGameEvent].
 */
class GameActions(
    private val bus: EventBus,
    private val animator: TileAnimator,
    private val scoringRenderer: ScoringRenderer,
    private val gameFlow: GameFlow,
    private val levelCheck: LevelCheck,
    private val checkRenderer: LevelCheckRenderer,
    private val level: Level,
    private val fieldRenderer: GameFieldRenderer,
    private val mechanics: GameMechanics,
) : InjectorDependency {

    companion object {
        val log = Logger<GameActions>()

        suspend operator fun invoke(injector: Injector): GameActions {
            injector.mapSingleton {
                GameActions(get(), get(), get(), get(), get(), get(), get(), get(), get())
            }
            return injector.get()
        }
    }

    override fun init(injector: Injector) {
        bus.register<ResetGameEvent> { reloadLevel() }
        bus.register<ShuffleGameEvent> { shuffle() }
    }

    private fun resetState() {
        animator.reset()
        scoringRenderer.reset()
        gameFlow.reset()
        levelCheck.reset()
        gameFlow.checkNewField()
        checkRenderer.update()
    }

    private fun shuffle() {
        log.debug { "Shuffle & Reset" }
        resetState()
        val field = level.field
        field.listAllPositions().forEach { position ->
            do {
                field[position] = Tile.randomTile()
            } while (mechanics.isInRowWithThree(position))
        }
        fieldRenderer.updateImagesFromField()
    }

    private fun reloadLevel() {
        log.debug { "Reload level" }
        level.reset()
        resetState()
        fieldRenderer.updateImagesFromField()
    }
}
