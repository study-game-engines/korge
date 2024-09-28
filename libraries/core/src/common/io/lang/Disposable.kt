package korlibs.io.lang

import kotlinx.coroutines.DisposableHandle

fun AutoCloseable.toDisposable(): DisposableHandle = DisposableHandle { this.close() }
