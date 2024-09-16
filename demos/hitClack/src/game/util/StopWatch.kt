package de.tfr.game.util

import korlibs.time.PerformanceCounter

class StopWatch(var start: ms = PerformanceCounter.milliseconds.toLong()) {
    fun getTime(): ms = PerformanceCounter.milliseconds.toLong() - start
}
