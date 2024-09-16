package de.tfr.game.renderer

import de.tfr.game.lib.actor.Point
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Container
import korlibs.korge.view.anchor
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.scale
import korlibs.math.geom.degrees

import resolution

class LogoRenderer(val point: Point, val gameFieldSize: Double) : Point by point {

    suspend fun init(container: Container) = apply {
        container.image(resourcesVfs["images/hitclack_logo.png"].readBitmap()) {
            position(point.x - width / 2, point.y - gameFieldSize - 390)
        }
        container.image(resourcesVfs["images/korge_logo.png"].readBitmap()) {
            rotation = (+16).degrees
            anchor(.5, .5)
            scale(.2)
            position(resolution.width - 70, 70)
        }
    }
}