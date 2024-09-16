import de.tfr.game.HitKlack
import de.tfr.game.ui.DEVICE
import de.tfr.game.util.Resolution
import korlibs.korge.Korge
import korlibs.logger.Logger

val resolution = Resolution(width = 800, height = 1440)

const val disableSound = false

suspend fun main() = Korge(title = "HitKlack", width = resolution.width, height = resolution.height, bgcolor = DEVICE) {
    Logger.defaultLevel = Logger.Level.WARN // <- Change this for debugging
    addComponent(HitKlack(this).initGame(stage))
}
