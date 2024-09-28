package korlibs.io.async

import kotlinx.coroutines.*

actual val Dispatchers.ConcurrencyLevel: Int get() = maxOf(1, java.lang.Runtime.getRuntime().availableProcessors())
