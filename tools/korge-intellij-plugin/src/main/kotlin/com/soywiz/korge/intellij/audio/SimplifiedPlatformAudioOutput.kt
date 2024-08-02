package com.soywiz.korge.intellij.audio

import korlibs.audio.sound.AudioSamples
import korlibs.audio.sound.AudioSamplesInterleaved
import korlibs.audio.sound.DequeBasedPlatformAudioOutput
import korlibs.audio.sound.interleaved
import korlibs.datastructure.thread.NativeThread
import kotlin.coroutines.CoroutineContext

// @TODO: Move to KorAU and use in the backends
abstract class SimplifiedPlatformAudioOutput(
    coroutineContext: CoroutineContext,
    frequency: Int,
) : DequeBasedPlatformAudioOutput(coroutineContext, frequency) {
    var running = true
    var thread: NativeThread? = null

    init {
        start()
    }

    abstract val isAvailable: Boolean
    abstract fun doClose()
    abstract fun doPrepare(): Boolean
    abstract fun doGetSamplesPerChunk(): Int
    abstract fun doWrite(buff: AudioSamples, frames: Int)

    final override fun start() {
        running = true

        if (!isAvailable) return

        if (!doPrepare()) {
            return
        }

        thread = NativeThread {
            //println("ALSANativeSoundProvider: Started Sound thread!")
            val frames = doGetSamplesPerChunk()
            val buff = AudioSamples(nchannels, frames)
            try {
                mainLoop@ while (running) {
                    //while (availableRead < frames && running) Thread.sleep(1L)
                    readShorts(buff.data)
                    doWrite(buff, frames)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                doClose()
            }
        }.also {
            it.isDaemon = true
            it.start()
        }
    }

    final override fun stop() {
        running = false
        thread?.interrupt()
        if (!isAvailable) return
        //doClose()
    }
}
