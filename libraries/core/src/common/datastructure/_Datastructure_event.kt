package korlibs.datastructure.event

import korlibs.concurrent.lock.Lock
import korlibs.concurrent.thread.NativeThread
import korlibs.concurrent.thread.nativeThread
import korlibs.concurrent.thread.sleep
import korlibs.datastructure.TGenPriorityQueue
import korlibs.datastructure.pauseable.Pauseable
import korlibs.datastructure.pauseable.SyncPauseable
import korlibs.logger.Console
import korlibs.time.*
import kotlin.time.Duration
import kotlin.time.TimeSource

expect fun createPlatformEventLoop(precise: Boolean = true): SyncEventLoop

interface EventLoop : Pauseable, AutoCloseable {
    companion object
    fun setImmediate(task: () -> Unit)
    fun setTimeout(time: Duration, task: () -> Unit): AutoCloseable
    fun setInterval(time: Duration, task: () -> Unit): AutoCloseable
    fun setIntervalFrame(task: () -> Unit): AutoCloseable = setInterval(60.hz.duration, task)
}

fun EventLoop.setInterval(time: Frequency, task: () -> Unit): AutoCloseable = setInterval(time.duration, task)

abstract class BaseEventLoop : EventLoop, Pauseable {
    val runLock: Lock = Lock()
}

open class SyncEventLoop(var immediateRun: Boolean = false) : BaseEventLoop(), Pauseable {

    private val pauseable: SyncPauseable = SyncPauseable()
    override var paused: Boolean by pauseable::paused
    private val lock: Lock = Lock()
    private var running: Boolean = true

    class TimedTask(val eventLoop: SyncEventLoop, var now: Duration, val time: Duration, var interval: Boolean, val callback: () -> Unit) : Comparable<TimedTask>, AutoCloseable {

        var timeMark: Duration
            get() = now + time
            set(value) {
                now = value - time
            }

        override fun compareTo(other: TimedTask): Int {
            return timeMark.compareTo(other.timeMark)
        }

        override fun close() {
            interval = false
            eventLoop.timedTasks.remove(this)
        }

    }

    private val startTime: TimeSource.Monotonic.ValueTimeMark = TimeSource.Monotonic.markNow()
    var nowProvider: () -> Duration = { startTime.elapsedNow() }
    private val now: Duration get() = nowProvider()
    private val tasks: ArrayDeque<() -> Unit> = ArrayDeque()
    private val timedTasks: TGenPriorityQueue<TimedTask> = TGenPriorityQueue { a, b -> a.compareTo(b) }

    fun setImmediateFirst(task: () -> Unit) {
        lock {
            tasks.addFirst(task)
            lock.notify()
        }
    }

    override fun setImmediate(task: () -> Unit) {
        lock {
            tasks.addLast(task)
            lock.notify()
        }
    }

    override fun setTimeout(time: Duration, task: () -> Unit): AutoCloseable {
        return _queueAfter(time, interval = false, task = task)
    }

    override fun setInterval(time: Duration, task: () -> Unit): AutoCloseable {
        return _queueAfter(time, interval = true, task = task)
    }

    private fun _queueAfter(time: Duration, interval: Boolean, task: () -> Unit): AutoCloseable {
        return lock {
            val task: TimedTask = TimedTask(this, now, time, interval, task)
            if (running) {
                timedTasks.add(task)
            } else {
                Console.warn("WARNING: QUEUED TASK time=$time interval=$interval without running")
            }
            lock.notify()
            task
        }
    }

    override fun close() {
        val oldImmediateRun = immediateRun
        try {
            immediateRun = true
            runAvailableNextTasks()
            running = false
        } finally {
            immediateRun = oldImmediateRun
        }
    }

    fun shouldTimedTaskRun(task: TimedTask): Boolean {
        if (immediateRun) return true
        return now >= timedTasks.head.timeMark
    }

    fun wait(waitTime: Duration) {
        if (immediateRun) return
        lock {
            lock.wait(waitTime)
        }
    }

    fun runAvailableNextTasks(runTimers: Boolean = true): Int {
        var count = 0
        while (runAvailableNextTask(runTimers)) {
            count++
        }
        return count
    }

    var uncatchedExceptionHandler: (Throwable) -> Unit = { it.printStackTrace() }

    private inline fun runCatchingExceptions(block: () -> Unit) {
        try {
            run {
                block()
            }
        } catch (e: Throwable) {
            uncatchedExceptionHandler(e)
        }
    }

    fun runAvailableNextTask(maxCount: Int): Boolean {
        for (n in 0 until maxCount) {
            if (!runAvailableNextTask()) return false
        }
        return true
    }

    fun runAvailableNextTask(runTimers: Boolean = true): Boolean {
        val timedTask = lock {
            if (runTimers) if (timedTasks.isNotEmpty() && shouldTimedTaskRun(timedTasks.head)) timedTasks.removeHead() else null else null
        }
        if (timedTask != null) {
            runCatchingExceptions { timedTask.callback() }
            if (timedTask.interval && !immediateRun) {
                timedTask.timeMark = maxOf(timedTask.timeMark + timedTask.time, now)
                timedTasks.add(timedTask)
            }
        }
        val task = lock {
            if (tasks.isNotEmpty()) tasks.removeFirst() else null
        }
        runCatchingExceptions {
            task?.invoke()
        }

        return task != null || timedTask != null
    }

    fun waitAndRunNextTask(): Boolean {
        lock {
            if (tasks.isEmpty() && timedTasks.isNotEmpty()) {
                val head = timedTasks.head
                val waitTime = head.timeMark - now
                if (waitTime >= 0.seconds) {
                    wait(waitTime)
                }
            }
        }

        return runAvailableNextTask()
    }

    fun runTasksUntilEmpty() {
        val stopwatch = Stopwatch().start()
        while (running) {
            pauseable.checkPaused()
            val somethingExecuted = waitAndRunNextTask()
            if (lock { !somethingExecuted && tasks.isEmpty() && timedTasks.isEmpty() }) {
                break
            }
            if (stopwatch.elapsed >= 0.1.seconds) {
                stopwatch.restart()
                NativeThread.sleep(10.milliseconds)
            }
        }
    }

    fun runTasksForever(runWhile: () -> Boolean = { true }) {
        running = true
        while (running && runWhile()) {
            runTasksUntilEmpty()
            NativeThread.sleep(1.milliseconds)
        }
    }

    private var thread: NativeThread? = null

    open fun start() {
        if (thread != null) return
        thread = nativeThread {
            runTasksForever { running }
        }
    }

    open fun stop() {
        running = false
        thread = null
    }

}
