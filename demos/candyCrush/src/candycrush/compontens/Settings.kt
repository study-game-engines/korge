package j4k.candycrush.compontens

import j4k.candycrush.ResetGameEvent
import j4k.candycrush.ShuffleGameEvent
import j4k.candycrush.audio.JukeBox
import j4k.candycrush.audio.SoundMachine
import j4k.candycrush.lib.EventBus
import j4k.candycrush.lib.Resources
import korlibs.inject.Injector
import korlibs.korge.view.Container
import korlibs.korge.view.View
import korlibs.korge.view.addTo

class Settings(
    private val view: View,
    private val res: Resources,
    private val bus: EventBus,
    private val soundMachine: SoundMachine,
    private val jukeBox: JukeBox
) : Container() {

    private val enabledSoundsOnStart = true
    private val enabledMusicOnStart = false

    companion object {
        suspend operator fun invoke(injector: Injector): Settings {
            injector.run {
                return Settings(get(), get(), get(), get(), get()).also {
                    injector.mapInstance(it)
                    it.init()
                }
            }
        }
    }

    suspend fun init() {
        val padding = 15.0
        val size = 80.0
        var position = -1
        fun yPos() = (size * ++position) + (padding * (position + 1))
        val settings = CheckBox(
            res.imageGuiSettingsOn, res.imageGuiSettings, res.imageGuiSettingsHover,
            initial = false
        ).apply {
            setSizeScaled(size, size)
            y = yPos()
        }.addTo(this)

        val sounds = CheckBox(res.imageGuiSoundOn, res.imageGuiSoundOff, initial = enabledSoundsOnStart) { activated ->
            soundMachine.enabled = activated
        }.apply {
            setSizeScaled(size, size)
            y = yPos()
            visible = false
        }.addTo(this)

        val music = CheckBox(res.imageGuiMusicOn, res.imageGuiMusicOff, initial = enabledMusicOnStart) { activated ->
            if (activated) {
                jukeBox.play()
            } else {
                jukeBox.stop()
            }
        }.apply {
            setSizeScaled(size, size)
            y = yPos()
            visible = false
        }.addTo(this)

        val shuffle = Button(res.imageGuiShuffle, res.imageGuiShuffleHover, res.imageGuiShuffleClick) {
            bus.send(ShuffleGameEvent)
        }.apply {
            setSizeScaled(size, size)
            y = yPos()
            visible = false
        }.addTo(this)

        val restart = Button(res.imageGuiRestart, res.imageGuiRestartHover, res.imageGuiRestartClick) {
            bus.send(ResetGameEvent)
        }.apply {
            setSizeScaled(size, size)
            y = yPos()
            visible = false
        }.addTo(this)

        settings.action = { checked ->
            listOf(music, sounds, shuffle, restart).forEach { it.visible = checked }
        }
        alignRightToRightOf(view, padding)
    }
}
