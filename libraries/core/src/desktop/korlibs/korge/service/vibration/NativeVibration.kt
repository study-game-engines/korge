package korlibs.korge.service.vibration

import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

actual class NativeVibration actual constructor(val coroutineContext: CoroutineContext) {
    /**
     * @param timings list of alternating ON-OFF durations in milliseconds. Staring with ON.
     * @param amplitudes list of intensities of the vibration. A `0.2` results in 20% vibration power.
     */
    @ExperimentalUnsignedTypes
    actual fun vibratePattern(timings: Array<Duration>, amplitudes: Array<Double>) {
    }

    /**
     * @param time vibration duration in milliseconds
     * @param amplitude percentage intensity of the vibration. A `0.2` results in 20% vibration power.
     */
    @ExperimentalUnsignedTypes
    actual fun vibrate(time: Duration, amplitude: Double) {
    }
}
