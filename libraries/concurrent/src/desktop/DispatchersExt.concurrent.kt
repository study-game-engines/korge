package korlibs.io.async

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext

actual val Dispatchers.CIO: CoroutineDispatcher get() = Dispatchers.IO

actual fun Dispatchers._createFixedThreadDispatcher(name: String, threadCount: Int): CoroutineDispatcher {
    //println("Dispatchers.createSingleThreadedDispatcher['$name'] : Platform.hasMultithreadedSharedHeap=${Platform.hasMultithreadedSharedHeap}")
    return newFixedThreadPoolContext(threadCount, name)
}
