@file:Suppress("PackageDirectoryMismatch")

package korlibs.datastructure.sync

import korlibs.concurrent.lock.*
import korlibs.datastructure.*

open class SynchronizedCollection<T>(
    val base: MutableCollection<T>,
    val lock: NonRecursiveLock = NonRecursiveLock()
) : MutableCollection<T> {
    override val size: Int get() = lock { base.size }
    override fun clear() = lock { base.clear() }
    override fun addAll(elements: Collection<T>): Boolean = lock { base.addAll(elements) }
    override fun add(element: T): Boolean = lock { base.add(element) }
    override fun isEmpty(): Boolean = lock { base.isEmpty() }
    override fun iterator(): MutableIterator<T> = SynchronizedMutableIterator(lock { base.iterator() }, lock)
    override fun retainAll(elements: Collection<T>): Boolean = lock { base.retainAll(elements) }
    override fun removeAll(elements: Collection<T>): Boolean = lock { base.removeAll(elements) }
    override fun remove(element: T): Boolean = lock { base.remove(element) }
    override fun containsAll(elements: Collection<T>): Boolean = lock { base.containsAll(elements) }
    override fun contains(element: T): Boolean = lock { base.contains(element) }
}

open class SynchronizedList<T>(
    val base: MutableList<T>,
    val lock: NonRecursiveLock = NonRecursiveLock()
) : BaseMutableList<T> {
    override fun clear() = lock { base.clear() }
    override fun add(index: Int, element: T) { lock { base.add(index, element) } }
    override fun addAll(index: Int, elements: Collection<T>): Boolean = lock { base.addAll(index, elements) }
    override val size: Int get() = lock { base.size }
    override fun get(index: Int): T = lock { base[index] }
    override fun removeAt(index: Int): T = lock { base.removeAt(index) }
    override fun set(index: Int, element: T): T = lock { base.set(index, element) }
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = SynchronizedBaseSubMutableList(this, fromIndex, toIndex)

    private inner class SynchronizedBaseSubMutableList<T>(mlist: MutableList<T>, start: Int, end: Int) : BaseSubMutableList<T>(mlist, start, end) {
        override fun add(index: Int, element: T) = lock { super.add(index, element) }
        override fun addAll(index: Int, elements: Collection<T>): Boolean = lock { super.addAll(index, elements) }
        override fun removeAt(index: Int): T = lock { super.removeAt(index) }
        override fun set(index: Int, element: T): T = lock { super.set(index, element) }
    }
}

open class SynchronizedMap<K, V>(
    val base: MutableMap<K, V>,
    val lock: NonRecursiveLock = NonRecursiveLock()
) : BaseMutableMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> get() = SynchronizedSet(base.entries)
    override val keys: MutableSet<K> get() = SynchronizedSet(base.keys)
    override val size: Int get() = base.size
    override val values: MutableCollection<V> get() = SynchronizedCollection(base.values, lock)
    override fun clear() { lock { base.clear() } }
    override fun remove(key: K): V? = lock { base.remove(key) }
    override fun put(key: K, value: V): V? = lock { base.put(key, value) }
    override fun get(key: K): V? = lock { base[key] }
    override fun putAll(from: Map<out K, V>) { lock { base.putAll(from) } }
    override fun isEmpty(): Boolean = lock { base.isEmpty() }
    override fun containsKey(key: K): Boolean = lock { base.containsKey(key) }
    override fun containsValue(value: V): Boolean = lock { base.containsValue(value) }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SynchronizedMap<*, *>) return false
        return lock { this.base == other.base }
    }
    override fun hashCode(): Int = lock { base.hashCode() }
    override fun toString(): String = lock { base.toString() }
}

open class SynchronizedMutableIterator<T>(
    val iterator: MutableIterator<T>,
    val lock: NonRecursiveLock = NonRecursiveLock()
) : MutableIterator<T> {
    override fun hasNext(): Boolean = lock { iterator.hasNext() }
    override fun next(): T = lock { iterator.next() }
    override fun remove()  = lock { iterator.remove() }
}

open class SynchronizedSet<T>(
    val base: MutableSet<T>,
    val lock: NonRecursiveLock = NonRecursiveLock()
) : MutableSet<T> {
    override fun add(element: T): Boolean = lock { base.add(element) }
    override fun addAll(elements: Collection<T>): Boolean = lock { base.addAll(elements) }
    override val size: Int get() = lock { base.size }
    override fun clear() = lock { base.clear() }
    override fun isEmpty(): Boolean = lock { base.isEmpty() }
    override fun containsAll(elements: Collection<T>): Boolean = lock { base.containsAll(elements) }
    override fun contains(element: T): Boolean = lock { base.contains(element) }
    override fun retainAll(elements: Collection<T>): Boolean = lock { base.retainAll(elements) }
    override fun removeAll(elements: Collection<T>): Boolean = lock { base.removeAll(elements) }
    override fun remove(element: T): Boolean  = lock { base.remove(element) }
    override fun iterator(): MutableIterator<T> = SynchronizedMutableIterator(lock { base.iterator() }, this.lock)
}
