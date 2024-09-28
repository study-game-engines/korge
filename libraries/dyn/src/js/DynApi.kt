package korlibs.io.dynamic

private external val globalThis: dynamic

internal actual object DynamicInternal : DynApi {

    actual override val global: Any get() = globalThis as Any

    actual override fun get(instance: Any?, key: String): Any? {
        return (instance.asDynamic())[key]
    }

    actual override fun set(instance: Any?, key: String, value: Any?) {
        (instance.asDynamic())[key] = value
    }

    actual override fun invoke(instance: Any?, key: String, args: Array<out Any?>): Any? {
        return (instance.asDynamic())[key].apply(instance, args)
    }

}
