package de.tfr.game.libgx.emu

import korlibs.korge.view.Container
import korlibs.korge.view.Graphics


open class ApplicationAdapter : ApplicationListener {
    override suspend fun create(container: Container) {
    }

    override fun resize(width: Int, height: Int) {
    }

    override suspend fun render(renderer: Graphics) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

}