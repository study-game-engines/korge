package korlibs.graphics.gl

import korlibs.graphics.AGFactory

actual object AGOpenglFactory {
    actual fun create(nativeComponent: Any?): AGFactory = AGFactoryWebgl
    actual val isTouchDevice: Boolean get() = js("('ontouchstart' in window || navigator.maxTouchPoints)").unsafeCast<Boolean>()
}
