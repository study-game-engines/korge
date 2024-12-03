@file:OptIn(CoreTimeInternalApi::class)

package korlibs.time

import korlibs.time.core.*
import korlibs.time.core.internal.*
import korlibs.time.core.internal.CoreTimeInternal.MILLIS_PER_DAY
import korlibs.time.core.internal.CoreTimeInternal.MILLIS_PER_HOUR
import korlibs.time.core.internal.CoreTimeInternal.MILLIS_PER_MINUTE
import korlibs.time.core.internal.CoreTimeInternal.MILLIS_PER_SECOND
import korlibs.time.core.internal.CoreTimeInternal.MILLIS_PER_WEEK
import kotlin.jvm.*
import kotlin.math.*
import kotlin.time.*
import kotlin.time.Duration.Companion.milliseconds

@PublishedApi
internal val DURATION_NIL: Duration = (-0x001FFFFFFFFFFFF3L).toDuration(DurationUnit.NANOSECONDS)

inline val Duration.Companion.NIL: Duration get() = DURATION_NIL
//val Duration.Companion.ZERO get() = Duration.ZERO

/** [Duration] representing this number as [nanoseconds] or 1 / 1_000_000_000 [seconds]. */
inline val Long.nanoseconds: Duration get() = toDuration(DurationUnit.NANOSECONDS)
/** [Duration] representing this number as [microseconds] or 1 / 1_000_000 [seconds]. */
inline val Long.microseconds: Duration get() = toDuration(DurationUnit.MICROSECONDS)
/** [Duration] representing this number as [milliseconds] or 1 / 1_000 [seconds]. */
inline val Long.milliseconds: Duration get() = toDuration(DurationUnit.MILLISECONDS)
/** [Duration] representing this number as [seconds]. */
inline val Long.seconds: Duration get() = toDuration(DurationUnit.SECONDS)
/** [Duration] representing this number as [minutes] or 60 [seconds]. */
inline val Long.minutes: Duration get() = toDuration(DurationUnit.MINUTES)
/** [Duration] representing this number as [hours] or 3_600 [seconds]. */
inline val Long.hours: Duration get() = toDuration(DurationUnit.HOURS)
/** [Duration] representing this number as [days] or 86_400 [seconds]. */
inline val Long.days: Duration get() = toDuration(DurationUnit.DAYS)
/** [Duration] representing this number as [weeks] or 604_800 [seconds]. */
inline val Long.weeks: Duration get() = (this * 7).days

/** [Duration] representing this number as [nanoseconds] or 1 / 1_000_000_000 [seconds]. */
inline val Float.nanoseconds get() = this.toDouble().nanoseconds
/** [Duration] representing this number as [microseconds] or 1 / 1_000_000 [seconds]. */
inline val Float.microseconds get() = this.toDouble().microseconds
/** [Duration] representing this number as [milliseconds] or 1 / 1_000 [seconds]. */
inline val Float.milliseconds get() = this.toDouble().milliseconds
/** [Duration] representing this number as [seconds]. */
inline val Float.seconds get() = this.toDouble().seconds
/** [Duration] representing this number as [minutes] or 60 [seconds]. */
inline val Float.minutes get() = this.toDouble().minutes
/** [Duration] representing this number as [hours] or 3_600 [seconds]. */
inline val Float.hours get() = this.toDouble().hours
/** [Duration] representing this number as [days] or 86_400 [seconds]. */
inline val Float.days get() = this.toDouble().days
/** [Duration] representing this number as [weeks] or 604_800 [seconds]. */
inline val Float.weeks get() = this.toDouble().weeks

/** [Duration] representing this number as [nanoseconds] or 1 / 1_000_000_000 [seconds]. */
inline val Int.nanoseconds: Duration get() = this.toDouble().nanoseconds
/** [Duration] representing this number as [microseconds] or 1 / 1_000_000 [seconds]. */
inline val Int.microseconds: Duration get() = this.toDouble().microseconds
/** [Duration] representing this number as [milliseconds] or 1 / 1_000 [seconds]. */
inline val Int.milliseconds: Duration get() = this.toDouble().milliseconds
/** [Duration] representing this number as [seconds]. */
inline val Int.seconds: Duration get() = this.toDouble().seconds
/** [Duration] representing this number as [minutes] or 60 [seconds]. */
inline val Int.minutes: Duration get() = this.toDouble().minutes
/** [Duration] representing this number as [hours] or 3_600 [seconds]. */
inline val Int.hours: Duration get() = this.toDouble().hours
/** [Duration] representing this number as [days] or 86_400 [seconds]. */
inline val Int.days: Duration get() = this.toDouble().days
/** [Duration] representing this number as [weeks] or 604_800 [seconds]. */
inline val Int.weeks: Duration get() = this.toDouble().weeks

/** [Duration] representing this number as [nanoseconds] or 1 / 1_000_000_000 [seconds]. */
inline val Double.nanoseconds: Duration get() = toDuration(DurationUnit.NANOSECONDS)
/** [Duration] representing this number as [microseconds] or 1 / 1_000_000 [seconds]. */
inline val Double.microseconds: Duration get() = toDuration(DurationUnit.MICROSECONDS)
/** [Duration] representing this number as [milliseconds] or 1 / 1_000 [seconds]. */
inline val Double.milliseconds: Duration get() = toDuration(DurationUnit.MILLISECONDS)
/** [Duration] representing this number as [seconds]. */
inline val Double.seconds: Duration get() = toDuration(DurationUnit.SECONDS)
/** [Duration] representing this number as [minutes] or 60 [seconds]. */
inline val Double.minutes: Duration get() = toDuration(DurationUnit.MINUTES)
/** [Duration] representing this number as [hours] or 3_600 [seconds]. */
inline val Double.hours: Duration get() = toDuration(DurationUnit.HOURS)
/** [Duration] representing this number as [days] or 86_400 [seconds]. */
inline val Double.days: Duration get() = toDuration(DurationUnit.DAYS)
/** [Duration] representing this number as [weeks] or 604_800 [seconds]. */
inline val Double.weeks: Duration get() = (this * 7).days

inline fun Duration.Companion.fromNanoseconds(value: Double): Duration = value.nanoseconds
inline fun Duration.Companion.fromMicroseconds(value: Double): Duration = value.microseconds
inline fun Duration.Companion.fromMilliseconds(value: Double): Duration = value.milliseconds
inline fun Duration.Companion.fromSeconds(value: Double): Duration = value.seconds
inline fun Duration.Companion.fromMinutes(value: Double): Duration = value.minutes
inline fun Duration.Companion.fromHours(value: Double): Duration = value.hours
inline fun Duration.Companion.fromDays(value: Double): Duration = value.days
inline fun Duration.Companion.fromWeeks(value: Double): Duration = value.weeks

inline fun Duration.Companion.fromNanoseconds(value: Number): Duration = fromNanoseconds(value.toDouble())
inline fun Duration.Companion.fromMicroseconds(value: Number): Duration = fromMicroseconds(value.toDouble())
inline fun Duration.Companion.fromMilliseconds(value: Number): Duration = fromMilliseconds(value.toDouble())
inline fun Duration.Companion.fromSeconds(value: Number): Duration = fromSeconds(value.toDouble())
inline fun Duration.Companion.fromMinutes(value: Number): Duration = fromMinutes(value.toDouble())
inline fun Duration.Companion.fromHours(value: Number): Duration = fromHours(value.toDouble())
inline fun Duration.Companion.fromDays(value: Number): Duration = fromDays(value.toDouble())
inline fun Duration.Companion.fromWeeks(value: Number): Duration = fromWeeks(value.toDouble())

/**
 * Represents a span of time, with [milliseconds] precision.
 *
 * It is a value class wrapping [Double] instead of [Long] to work on JavaScript without allocations.
 */
@Deprecated("", replaceWith = ReplaceWith("kotlin.time.Duration"))
typealias TimeSpan = Duration

inline operator fun Duration.unaryPlus(): Duration = this

val Duration.milliseconds: Double get() = this.inWholeNanoseconds.toDouble() / 1_000_000.0

/** Returns the total number of [nanoseconds] for this [Duration] (1 / 1_000_000_000 [seconds]) */
inline val Duration.nanoseconds: Double get() = this.inWholeNanoseconds.toDouble()
/** Returns the total number of [nanoseconds] for this [Duration] (1 / 1_000_000_000 [seconds]) */
inline val Duration.nanosecondsLong: Long get() = this.inWholeNanoseconds
/** Returns the total number of [nanoseconds] for this [Duration] (1 / 1_000_000_000 [seconds]) as Integer */
inline val Duration.nanosecondsInt: Int get() = this.inWholeNanoseconds.toInt()

/** Returns the total number of [microseconds] for this [Duration] (1 / 1_000_000 [seconds]) */
inline val Duration.microseconds: Double get() = this.inWholeNanoseconds.toDouble() / 1_000.0
/** Returns the total number of [microseconds] for this [Duration] (1 / 1_000_000 [seconds]) as Integer */
inline val Duration.microsecondsInt: Int get() = microseconds.toInt()

/** Returns the total number of [seconds] for this [Duration] */
val Duration.seconds: Double get() = this.milliseconds / MILLIS_PER_SECOND
/** Returns the total number of [minutes] for this [Duration] (60 [seconds]) */
val Duration.minutes: Double get() = this.milliseconds / MILLIS_PER_MINUTE
/** Returns the total number of [hours] for this [Duration] (3_600 [seconds]) */
val Duration.hours: Double get() = this.milliseconds / MILLIS_PER_HOUR
/** Returns the total number of [days] for this [Duration] (86_400 [seconds]) */
val Duration.days: Double get() = this.milliseconds / MILLIS_PER_DAY
/** Returns the total number of [weeks] for this [Duration] (604_800 [seconds]) */
val Duration.weeks: Double get() = this.milliseconds / MILLIS_PER_WEEK

/** Returns the total number of [milliseconds] as a [Long] */
inline val Duration.millisecondsLong: Long get() = inWholeMilliseconds
/** Returns the total number of [milliseconds] as an [Int] */
inline val Duration.millisecondsInt: Int get() = inWholeMilliseconds.toInt()

@Deprecated("", ReplaceWith("milliseconds.milliseconds", "kotlin.time.Duration.Companion.milliseconds"))
inline fun TimeSpan(milliseconds: Double): Duration = milliseconds.milliseconds

inline operator fun Duration.times(scale: Float): Duration = this * scale.toDouble()
inline operator fun Duration.div(scale: Float): Duration = this / scale.toDouble()

inline infix fun Duration.divFloat(other: Duration): Float = (this.milliseconds / other.milliseconds).toFloat()
inline operator fun Duration.rem(other: Duration): Duration = (this.milliseconds % other.milliseconds).milliseconds
inline infix fun Duration.umod(other: Duration): Duration = (this.milliseconds umod other.milliseconds).milliseconds

/** Return true if [Duration.NIL] */
inline val Duration.isNil: Boolean get() = this == DURATION_NIL

fun Duration.roundMilliseconds(): Duration = kotlin.math.round(milliseconds).milliseconds
fun max(a: Duration, b: Duration): Duration = max(a.milliseconds, b.milliseconds).milliseconds
fun min(a: Duration, b: Duration): Duration = min(a.milliseconds, b.milliseconds).milliseconds
fun Duration.clamp(min: Duration, max: Duration): Duration = when {
    this < min -> min
    this > max -> max
    else -> this
}
inline fun Duration.coalesce(block: () -> Duration): Duration = if (this != Duration.NIL) this else block()

@OptIn(CoreTimeInternalApi::class)
fun Duration.Companion.now(): Duration = Duration.fromMilliseconds(CoreTime.currentTimeMillisDouble())
