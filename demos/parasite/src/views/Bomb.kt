package views

import korlibs.audio.sound.readSound
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.tween.tween
import korlibs.korge.view.Container
import korlibs.korge.view.anchor
import korlibs.korge.view.hitShape
import korlibs.korge.view.image
import korlibs.time.seconds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Bomb : Container() {
    
    enum class State {
        READY,
        EXPLODING
    }
    
    private lateinit var bombSound: NativeSound
    var state: State = State.READY
    
    suspend fun loadBomb() {
        state = State.READY
        bombSound = resourcesVfs["sounds/fx/bomb_fx.wav"].readSound().apply {
            volume -= .1
        }
        
        image(resourcesVfs["graphics/game_scene/bomb/bomb_exploding.png"].readBitmap()) {
            scale = .9
            anchor(.5, .5)
            smoothing = false
        }
        
        hitShape {
            circle(width / 2, height / 2, width / 2)
        }
        
        scale = 0.0
        
        visible = false
    }
    
    fun explode() {
        visible = true
        state = State.EXPLODING
        GlobalScope.launch {
            bombSound.play()
            this.tween(this::scale[.8], this::rotationDegrees[800], time = 1.seconds)
            this.tween(this::scale[3], this::rotationDegrees[rotationDegrees + 400], time = 0.2.seconds)
            this.tween(this::rotationDegrees[rotationDegrees + 400], time = 0.2.seconds)
            this.tween(this::scale[0], this::rotationDegrees[rotationDegrees + 400], time = 0.2.seconds)
            visible = false
            scale = .8
            rotationDegrees = 0.0
            state = State.READY
        }
    }
}