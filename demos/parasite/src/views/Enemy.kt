package views

import korlibs.audio.sound.readSound
import korlibs.image.color.Colors
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.time.delay
import korlibs.korge.tween.tween
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.Sprite
import korlibs.korge.view.SpriteAnimation
import korlibs.korge.view.hitShape
import korlibs.math.geom.Point
import korlibs.math.interpolation.EASE_IN_OUT
import korlibs.math.interpolation.Easing
import korlibs.time.milliseconds
import korlibs.time.seconds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Enemy(val direction: Point) : Container() {
    
    enum class State {
        READY,
        APPEARING,
        MOVING,
        DYING
    }
    
    enum class EnemyType {
        STANDARD,
        CHASER
    }
    
    private lateinit var dieSound: NativeSound
    private lateinit var portalSound: NativeSound
    
    private lateinit var movingView: Image
    private lateinit var appearingView: Sprite
    
    var moveSpeed: Double = 50.0
    var chaseRadius: Float = 0f
    
    lateinit var state: State
    var type: EnemyType = EnemyType.STANDARD
        set(value) {
            when (value) {
                EnemyType.STANDARD -> {
                    chaseRadius = 50f; moveSpeed = 50.0; tint = Colors.WHITE
                }
                EnemyType.CHASER -> {
                    chaseRadius = 200f; moveSpeed = 40.0; tint = Colors.ORANGE
                }
            }
            field = value
        }
    
    suspend fun loadEnemy() {
        state = Enemy.State.READY
        portalSound = resourcesVfs["sounds/fx/portal.wav"].readSound().apply {
            volume += .5
        }
        dieSound = resourcesVfs["sounds/fx/enemy_die.mp3"].readSound().apply {
            volume -= 1
        }
        val appearingViewMap = resourcesVfs["graphics/game_scene/enemy/enemy_appearing.png"].readBitmap()
        appearingView = Sprite(initialAnimation = SpriteAnimation(
                spriteMap = appearingViewMap,
                spriteWidth = 15,
                spriteHeight = 15,
                columns = 16,
                rows = 1
        ), smoothing = false, anchorX = .5)
        appearingView.spriteDisplayTime = 40.milliseconds
        
        movingView = Image(resourcesVfs["graphics/game_scene/enemy/enemy_idle.png"].readBitmap(),
                smoothing = false, anchorX = .5)
        
        addChild(appearingView)
        
        hitShape {
            circle(width / 2, height / 2, width / 2)
        }
    }
    
    fun live() {
        state = Enemy.State.APPEARING
        removeChildren()
        addChild(appearingView)
        GlobalScope.launch {
            this@Enemy.tween(this@Enemy::scale[2.0], time = .3.seconds, easing = Easing.EASE_IN_OUT)
            this@Enemy.tween(this@Enemy::scale[1.0], time = .4.seconds, easing = Easing.EASE_IN_OUT)
            state = Enemy.State.MOVING
        }
        appearingView.playAnimation()
        portalSound.play()
        appearingView.onAnimationCompleted.once {
            removeChildren()
            addChild(movingView)
            tint = tint
        }
    }
    
    fun infect(onInfect: () -> Unit) {
        state = Enemy.State.READY
        tint = Colors.DARKMAGENTA
        GlobalScope.launch {
            delay(.3.seconds)
            tint = Colors.WHITE
            removeChildren()
            onInfect()
        }
    }
    
    fun die(onDie: () -> Unit) {
        state = State.DYING
        removeChildren()
        addChild(appearingView)
        tint = Colors.DARKMAGENTA
        GlobalScope.launch {
            this@Enemy.tween(this@Enemy::scale[0.1], time = .5.seconds, easing = Easing.EASE_IN_OUT)
        }
        dieSound.play()
        appearingView.playAnimation(reversed = true)
        appearingView.onAnimationCompleted.once {
            state = Enemy.State.READY
            tint = Colors.WHITE
            onDie()
            removeChildren()
        }
    }
    
    fun resetEnemy(): Unit {
        state = State.READY
        scale = 1.0
    }
}