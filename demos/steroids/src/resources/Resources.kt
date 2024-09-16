package resources

import gameplay.registerProcessSystem
import korlibs.audio.sound.readSound
import korlibs.image.atlas.Atlas
import korlibs.image.atlas.readAtlas
import korlibs.image.font.BitmapFont
import korlibs.image.font.readBitmapFont
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Views
import kotlin.native.concurrent.*

class Resources(private val views: Views) {
    @ThreadLocal
    companion object{
        lateinit var steroidsAtlas: Atlas

        lateinit var font: BitmapFont
        lateinit var tubo5Sound: NativeSound
        lateinit var tubo8Sound: NativeSound
        lateinit var fx33Sound: NativeSound
        lateinit var naveSound: NativeSound

        private var loaded = false
        private var loadedGfx = false
        private var loadedMusic = false
    }

    suspend fun loadAll() {
        if(loaded) return
        loaded = true

        loadGfx()
        loadMusic()
    }

    suspend fun loadGfx() {
        if(loadedGfx) return
        loadedGfx = true

        steroidsAtlas = resourcesVfs["fpg.atlas.json"].readAtlas(views)
        font = resourcesVfs["texts/I-pixel-u.fnt"].readBitmapFont()

        fx33Sound = resourcesVfs["fx33.wav"].readSound()
        tubo5Sound = resourcesVfs["tubo5.wav"].readSound()
        tubo8Sound = resourcesVfs["tubo8.wav"].readSound()
        naveSound = resourcesVfs["nave.wav"].readSound()


    }

    suspend fun loadMusic() {
        if(loadedMusic) return
        loadedMusic = true

        //music = resourcesVfs["music.mp3"].readNativeSound(true)
    }

    fun setLoaded() {
        loaded = true
    }
}

