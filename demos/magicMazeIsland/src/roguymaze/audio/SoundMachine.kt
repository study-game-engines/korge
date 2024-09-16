package tfr.korge.jam.roguymaze.audio

import korlibs.audio.sound.readSound
import korlibs.inject.Injector
import korlibs.inject.InjectorDependency
import korlibs.io.file.std.resourcesVfs

/**
 * Plays game sounds.
 */
class SoundMachine : InjectorDependency {

    /**
     * Removing tiles from the field
     */
    private var clear: NativeSound? = null

    /**
     * Wrong tile move, which will be toggled back
     */
    private var wrongMove: NativeSound? = null

    /**
     * Tile git's the ground. Used for now.
     */
    private var dopGround: NativeSound? = null

    val playSounds = true

    companion object {
        operator fun invoke(injector: Injector) {
            injector.mapSingleton { SoundMachine() }
        }
    }

    private suspend fun newSound(fileName: String) = resourcesVfs["sounds/$fileName"].readSound()

    override fun init(injector: Injector) {
        if (playSounds) {
            clear = newSound("clear.mp3")
            wrongMove = newSound("wrong_move.mp3")
            dopGround = newSound("drop_ground.mp3")
        }
    }

    fun playClear() {
        if (playSounds) {
            clear?.play()
        }
    }

    fun playWrongMove() {
        if (playSounds) {
            wrongMove?.play()
        }
    }

    fun playDropGround() {
        if (playSounds) {
            dopGround?.play()
        }
    }


}