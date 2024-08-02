package korlibs.datastructure

import korlibs.concurrent.thread.*
import korlibs.time.*

//val NativeThread.extra: Extra get() {
//    if (this.userData == null) {
//        this.userData = ExtraMixin()
//    }
//    return this.userData as Extra
//}

// Extension from DateTime
fun NativeThread.Companion.sleepUntil(date: DateTime, exact: Boolean = true) {
    sleep(date - DateTime.now(), exact)
}

