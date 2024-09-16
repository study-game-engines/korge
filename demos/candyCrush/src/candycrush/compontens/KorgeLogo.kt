package j4k.candycrush.compontens

import j4k.candycrush.lib.loadImage
import korlibs.korge.tween.tween
import korlibs.korge.view.Stage
import korlibs.korge.view.anchor
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.scale
import korlibs.math.geom.degrees
import korlibs.math.interpolation.EASE_IN_OUT
import korlibs.math.interpolation.Easing
import korlibs.time.seconds

/**
 * Displays a wiggling korge engine logo on the screen.
 */
class KorgeLogo(private val view: Stage) {

    private val minDegrees = (-16).degrees
    private val maxDegrees = (+16).degrees

    suspend fun addLogo() {
        val korgeBitmap = loadImage("korge.png")

        val image = view.image(korgeBitmap) {
            rotation = maxDegrees
            anchor(.5, .5)
            scale(.2)
            position(1180, 80)
        }

        while (true) {
            image.tween(image::rotation[minDegrees], time = 2.seconds, easing = Easing.EASE_IN_OUT)
            image.tween(image::rotation[maxDegrees], time = 2.seconds, easing = Easing.EASE_IN_OUT)
        }
    }

}
