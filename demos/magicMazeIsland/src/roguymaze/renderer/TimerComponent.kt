package tfr.korge.jam.roguymaze.renderer

import korlibs.inject.Injector
import korlibs.korge.view.Container
import korlibs.korge.view.Text
import korlibs.korge.view.View
import korlibs.korge.view.Views
import korlibs.korge.view.align.alignRightToRightOf
import korlibs.korge.view.align.alignTopToTopOf
import korlibs.korge.view.text
import tfr.korge.jam.roguymaze.lib.Resources
import tfr.korge.jam.roguymaze.model.Countdown
import tfr.korge.jam.roguymaze.renderer.util.TimerFormatter

class TimerComponent(res: Resources, override val view: View) : Container(), UpdateComponentWithViews {

    private val formatter = TimerFormatter()
    private val stopWatch = Countdown(minutes = 12, seconds = 0)
    private val timerText: Text
    private var lastUpdate = 0.0

    init {
        timerText = text(getTime(), font = res.fontBubble, textSize = 46.0) {
            alignRightToRightOf(view, 14.0)
            alignTopToTopOf(view, 12.0)
        }
    }

    companion object {

        suspend operator fun invoke(injector: Injector): TimerComponent {
            injector.mapSingleton {
                TimerComponent(get(), get())
            }
            return injector.get()
        }
    }

    override fun update(views: Views, ms: Double) {
        lastUpdate += ms
        if (lastUpdate > 500) {
            updateTimer()
        }
    }

    fun updateTimer() {
        timerText.text = getTime()
    }

    fun getTime(): String {
        return formatter.getFormattedTimeAsString(stopWatch.getTime())
    }

}