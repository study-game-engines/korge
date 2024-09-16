package j4k.candycrush.audio

import korlibs.audio.sound.PlaybackParameters
import korlibs.audio.sound.Sound
import korlibs.audio.sound.SoundChannel
import korlibs.audio.sound.readMusic
import korlibs.inject.Injector
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Views
import kotlinx.coroutines.launch

/**
 * Jukebox which plays background music in a random order.
 */
class JukeBox(private val views: Views) {

    private val playList = mutableListOf<Sound>()
    private var playing = false
    private var currentSong: SoundChannel? = null
    private var index = -1

    companion object {
        suspend operator fun invoke(injector: Injector): JukeBox {
            injector.mapSingleton { JukeBox(get()) }
            return injector.get()
        }
    }

    private suspend fun loadPlaylist() {
        playList += newMusic("monkey_drama.mp3")
        playList += newMusic("monkey_island_puzzler.mp3")
    }

    suspend fun play() {
        loadIfNeeded()
        playing = true
        if (currentSong == null) {
            loopMusicPlaylist()
        }
    }

    private suspend fun loadIfNeeded() {
        if (playList.isEmpty()) {
            loadPlaylist()
        }
    }

    private fun nextSong(): Sound {
        index += 1
        if (index >= playList.size) {
            index = 0
        }
        return playList[index]
    }

    private fun startNextSong() {
        if (playing) {
            views.launch() {
                loopMusicPlaylist()
            }
        }
    }

    private suspend fun loopMusicPlaylist() {
        while (currentSong == null) {
            val nextSong: Sound = nextSong()
            currentSong = nextSong.play(PlaybackParameters(onFinish = {
                startNextSong()
            }))
        }
    }

    fun stop() {
        playing = false
        val currentSong = currentSong
        this.currentSong = null
        currentSong?.stop()
    }

}

private suspend fun newMusic(fileName: String): Sound = resourcesVfs["music/$fileName"].readMusic()
