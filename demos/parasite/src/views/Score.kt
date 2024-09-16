package views

import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.time.delay
import korlibs.korge.tween.tween
import korlibs.korge.view.Container
import korlibs.korge.view.Image
import korlibs.korge.view.Text
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.text
import korlibs.math.interpolation.EASE_IN_OUT
import korlibs.math.interpolation.Easing
import korlibs.time.seconds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.round

class Score : Container() {
    
    var counter = 0.0
    private lateinit var score: Text
    private lateinit var lives: Text
    private lateinit var bg: Image
    
    suspend fun loadScore() {
        
        bg = image(resourcesVfs["graphics/game_scene/score_bg.png"].readBitmap()) {
            smoothing = false
        }
        score = text("0") {
            color = RGBA(0, 72, 124)
            filtering = false
            textSize = 8.0
            position(16, 36)
        }
        lives = text("xxx") {
            color = RGBA(0, 72, 124)
            filtering = false
            textSize = 5.5
            position(62, 31)
        }
    }
    
    fun addAditionalPoints(points: Int) {
        GlobalScope.launch {
            score.tween(score::scale[1.1], time = .1.seconds, easing = Easing.EASE_IN_OUT)
            counter += points
            score.text = round(counter).toInt().toString()
            delay(.1.seconds)
            score.tween(score::scale[1.0], time = .1.seconds, easing = Easing.EASE_IN_OUT)
        }
    }
    
    fun updateLivesCounter(lives: Int) {
        when (lives) {
            2 -> {
                this.lives.color = Colors.YELLOW; this.lives.text = "xx"
            }
            1 -> {
                this.lives.color = Colors.RED; this.lives.text = "x"
            }
            0 -> {
                this.lives.color = Colors.DARKRED; this.lives.text = "---"
            }
        }
        GlobalScope.launch {
            delay(.1.seconds)
            tween(this@Score.lives::scale[5.0], time = .2.seconds)
            delay(.3.seconds)
            tween(this@Score.lives::scale[1.0], time = .2.seconds)
        }
    }
}