package tfr.korge.jam.roguymaze.audio

import korlibs.inject.Injector
import korlibs.inject.InjectorDependency
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Stage
import korlibs.time.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Jukebox which plays background music in a random order.
 */
class JukeBox(val stage: Stage) : InjectorDependency {

    private var playing: NativeSoundChannel? = null
    private val playList = mutableListOf<NativeSound>()
    private var started = false
    var activated = false

    companion object {
        suspend operator fun invoke(injector: Injector, receiver: JukeBox.() -> Unit): JukeBox {
            injector.mapSingleton {
                JukeBox(get()).apply {
                    receiver.invoke(this)
                }
            }
            return injector.get()
        }
    }

    override fun init(injector: Injector) {
        if (activated) {
            //playList += newMusic("someMusic.mp3")
        }
    }

    fun play() {
        if (activated && !started) {
            stage.launch {
                loopMusicPlaylist()
            }
        }
    }

    private suspend fun loopMusicPlaylist() {
        started = true
        while (started) {
            val nextSong: NativeSound = playList.random()
            playing = nextSong.play()
            delay(nextSong.length.coerceAtLeast(2.seconds))
            playing?.stop()
        }
    }

    fun stop() {
        playing?.stop()
        started = false
    }


}

private suspend fun newMusic(fileName: String): NativeSound = resourcesVfs["music/$fileName"].readNativeSound(
    streaming = true
)