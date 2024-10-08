package korlibs.datastructure

import kotlinx.atomicfu.locks.*
import kotlin.reflect.*

class ExtraObject : MutableMap<String, Any?> {
    private val lock = reentrantLock()
    private inline fun <T> lock(block: () -> T): T = lock.withLock(block)
    //private val data = FastStringMap<Any?>()
    private val data = HashMap<String, Any?>()
    override val size: Int get() = lock { data.size }
    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> get() = lock { data.keys.associateWith { data[it] }.toMutableMap().entries.toMutableSet() }
    override val keys: MutableSet<String> get() = lock { data.keys.toMutableSet() }
    override val values: MutableCollection<Any?> = lock { data.values.toMutableList() }
    override operator fun get(key: String): Any? = lock { data[key] }
    operator fun set(key: String, value: Any?) = lock { data[key] = value }
    operator fun contains(key: String): Boolean = lock { key in data }
    override fun isEmpty(): Boolean = size == 0
    override fun clear() = lock { data.clear() }
    override fun remove(key: String): Any? = lock { data.remove(key) }
    override fun putAll(from: Map<out String, Any?>) = lock { for ((key, value) in from) data[key] = value }
    override fun put(key: String, value: Any?): Any? = lock { data[key] = value }
    override fun containsValue(value: Any?): Boolean = lock { value in values }
    override fun containsKey(key: String): Boolean = lock { key in data }
}

typealias ExtraType = ExtraObject?
fun ExtraTypeCreate() = ExtraObject()

interface Extra {

    var extra: ExtraType

    open class Mixin(override var extra: ExtraType = null) : Extra

    companion object {
        operator fun invoke() = Mixin()
    }

    @Suppress("UNCHECKED_CAST")
    class Property<T : Any?>(val name: String? = null, val defaultGen: () -> T) {
        inline operator fun getValue(thisRef: Extra, property: KProperty<*>): T {
            //val res = (thisRef.extra?.get(name ?: property.name).fastCastTo<T?>())
            val res = (thisRef.extra as? MutableMap<String, T?>)?.get(name ?: property.name)
            if (res == null) {
                val r = defaultGen()
                if (r != null || thisRef.hasExtra(name ?: property.name)) {
                    setValue(thisRef, property, r)
                }
                return r
            }
            return res
        }

        inline operator fun setValue(thisRef: Extra, property: KProperty<*>, value: T) {
            //beforeSet(value)
            thisRef.setExtra(name ?: property.name, value)
            //afterSet(value)
        }
    }

    class PropertyThis<T2 : Extra, T : Any?>(val name: String? = null, val defaultGen: T2.() -> T) {
        @PublishedApi internal var transform: (T2.(value: T) -> T) = { it }

        inline fun withTransform(noinline block: T2.(T) -> T): PropertyThis<T2, T> { transform = block; return this }

        inline operator fun getValue(thisRef: T2, property: KProperty<*>): T {
            val res = thisRef.getExtraTyped<T>(name ?: property.name)
            if (res == null) {
                val r = defaultGen(thisRef)
                setValueUntransformed(thisRef, property, r)
                return r
            }
            return res
        }

        inline fun setValueUntransformed(thisRef: T2, property: KProperty<*>, value: T) {
            thisRef.setExtra(name ?: property.name, value)
        }

        inline operator fun setValue(thisRef: T2, property: KProperty<*>, value: T) {
            setValueUntransformed(thisRef, property, transform(thisRef, value))
        }
    }
}

fun <T> Extra.extraCache(name: String, block: () -> T): T = (getExtra(name) as? T?) ?: block().also { setExtra(name, it) }
//fun <T : Any?> Extra.getExtraTyped(name: String): T? = (extra as? MutableMap<String, T?>)?.get(name).fastCastTo<T?>()
@Suppress("UNCHECKED_CAST") fun <T : Any?> Extra.getExtraTyped(name: String): T? = (extra as? MutableMap<String, T?>)?.get(name)
fun Extra.hasExtra(name: String): Boolean = extra?.contains(name) == true
fun Extra.getExtra(name: String): Any? = extra?.get(name)
fun Extra.setExtra(name: String, value: Any?) {
    if (extra == null) {
        if (value == null) return
        extra = ExtraTypeCreate()
    }
    extra?.set(name, value)
}

inline fun <T> extraProperty(name: String? = null, noinline default: () -> T) = Extra.Property(name, default)
inline fun <T2 : Extra, T> extraPropertyThis(name: String? = null, noinline transform: T2.(T) -> T = { it }, noinline default: T2.() -> T): Extra.PropertyThis<T2, T> = Extra.PropertyThis(name, default).withTransform(transform)
