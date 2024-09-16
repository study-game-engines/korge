package views

import korlibs.audio.sound.readSound
import korlibs.image.color.Colors
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.time.delay
import korlibs.korge.tween.tween
import korlibs.korge.view.BlendMode
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.Sprite
import korlibs.korge.view.SpriteAnimation
import korlibs.korge.view.hitShape
import korlibs.korge.view.position
import korlibs.math.interpolation.EASE_IN
import korlibs.math.interpolation.EASE_IN_OUT
import korlibs.math.interpolation.Easing
import korlibs.time.milliseconds
import korlibs.time.seconds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Player : Container() {
    
    enum class State {
        READY,
        APPEARING,
        MOVING,
        DYING,
        HURT
    }
    
    var lives: Int = 3
    var moveSpeed = 100.0
    lateinit var state: State
    var isTeleportActive: Boolean = true
    
    private lateinit var hurtSound: NativeSound
    private lateinit var teleportSound: NativeSound
    private lateinit var portalSound: NativeSound
    
    var bomb: Bomb = Bomb()
    private lateinit var idleView: Image
    private lateinit var appearingView: Sprite
    
    suspend fun loadPlayer() {
        state = State.READY
        
        portalSound = resourcesVfs["sounds/fx/portal.wav"].readSound().apply {
            volume += .5
        }
        teleportSound = resourcesVfs["sounds/fx/teleport.wav"].readSound().apply {
            volume += 1
        }
        hurtSound = resourcesVfs["sounds/fx/player_hurt.wav"].readSound()
        
        val appearingViewMap = resourcesVfs["graphics/game_scene/player/player_appearing.png"].readBitmap()
        appearingView = Sprite(initialAnimation = SpriteAnimation(
                spriteMap = appearingViewMap,
                spriteWidth = 15,
                spriteHeight = 15,
                columns = 16,
                rows = 1
        ), smoothing = false, anchorX = .5)
        appearingView.spriteDisplayTime = 40.milliseconds
        
        idleView = Image(resourcesVfs["graphics/game_scene/player/player_idle.png"].readBitmap(),
                smoothing = false, anchorX = .5)
        bomb.loadBomb()
        
        addChild(appearingView)
        
        hitShape {
            circle(width / 2, height / 2, width / 2)
        }
    }
    
    fun live() {
        state = State.APPEARING
        removeChildren()
        addChild(appearingView)
        GlobalScope.launch {
            this@Player.tween(this@Player::scale[2.0], time = .3.seconds, easing = Easing.EASE_IN_OUT)
            this@Player.tween(this@Player::scale[1.0], time = .4.seconds, easing = Easing.EASE_IN_OUT)
        }
        appearingView.playAnimation()
        portalSound.play()
        appearingView.onAnimationCompleted.once {
            state = State.MOVING
            removeChildren()
            addChild(idleView)
        }
    }
    
    fun teleport(x: Double, y: Double, onTeleport: () -> Unit) {
        state = State.APPEARING
        blendMode = BlendMode.SCREEN
        teleportSound.play()
        GlobalScope.launch {
            this@Player.tween(this@Player::x[x], this@Player::y[y], time = .2.seconds, easing = Easing.EASE_IN)
            onTeleport()
            this@Player.tween(this@Player::scale[2.0], time = .3.seconds, easing = Easing.EASE_IN_OUT)
            this@Player.tween(this@Player::scale[1.0], time = .4.seconds, easing = Easing.EASE_IN_OUT)
            blendMode = BlendMode.NORMAL
            state = State.MOVING
            removeChildren()
            addChild(idleView)
        }
    }
    
    fun die(onDie: () -> Unit) {
        state = State.DYING
        removeChildren()
        addChild(appearingView)
        tint = Colors.BLUE
        GlobalScope.launch {
            this@Player.tween(this@Player::scale[1.0], time = .2.seconds, easing = Easing.EASE_IN_OUT)
            this@Player.tween(this@Player::scale[0.1], time = .4.seconds, easing = Easing.EASE_IN_OUT)
        }
        portalSound.play()
        appearingView.playAnimation(reversed = true)
        appearingView.onAnimationCompleted.once {
            state = State.READY
            tint = Colors.WHITE
            onDie()
            removeChildren()
        }
    }
    
    fun dropBomb(x: Double, y: Double) {
        bomb.position(x, y)
        bomb.explode()
    }
    
    fun hurt() {
        state = State.HURT
        lives--
        hurtSound.play()
        GlobalScope.launch {
            tint = Colors.RED
            delay(1.seconds)
            state = State.MOVING
            tint = Colors.WHITE
        }
    }
}