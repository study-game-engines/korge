package korlibs.datastructure

import kotlin.math.*

// ArrayList that prevents isObject + jsInstanceOf on getter on Kotlin/JS. This class should be temporal until Kotlin/JS fixes this issue.
expect class FastArrayList<E> : MutableListEx<E>, RandomAccess {

    constructor()
    constructor(initialCapacity: Int)
    constructor(elements: Collection<E>)

    fun trimToSize()
    fun ensureCapacity(minCapacity: Int)

    /*MutableListEx*/

    override fun removeRange(fromIndex: Int, toIndex: Int)

    /*List*/

    override val size: Int
    override fun isEmpty(): Boolean
    override fun contains(element: @UnsafeVariance E): Boolean
    override fun containsAll(elements: Collection<@UnsafeVariance E>): Boolean
    override operator fun get(index: Int): E
    override fun indexOf(element: @UnsafeVariance E): Int
    override fun lastIndexOf(element: @UnsafeVariance E): Int

    /*MutableCollection*/

    override fun iterator(): MutableIterator<E>

    /*MutableList*/

    override fun add(element: E): Boolean
    override fun remove(element: E): Boolean
    override fun addAll(elements: Collection<E>): Boolean
    override fun addAll(index: Int, elements: Collection<E>): Boolean
    override fun removeAll(elements: Collection<E>): Boolean
    override fun retainAll(elements: Collection<E>): Boolean
    override fun clear()
    override operator fun set(index: Int, element: E): E
    override fun add(index: Int, element: E)
    override fun removeAt(index: Int): E
    override fun listIterator(): MutableListIterator<E>
    override fun listIterator(index: Int): MutableListIterator<E>
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E>
    inline fun fastForEach(callback: (E) -> Unit)
    inline fun fastForEachWithIndex(callback: (index: Int, value: E) -> Unit)
    inline fun fastForEachReverse(callback: (E) -> Unit)
    inline fun fastForEachReverseWithIndex(callback: (index: Int, value: E) -> Unit)

}

fun <T> fastArrayListOf(vararg values: T): FastArrayList<T> = FastArrayList<T>(values.size).also { it.addAll(values) }

fun <T> List<T>.ensureFastList(): FastArrayList<T> = if (this is FastArrayList) this else FastArrayList<T>(this.size).also { out -> fastForEach { out.add(it) } }

fun <T> List<T>.toFastList(): List<T> = FastArrayList<T>(this.size).also { out -> fastForEach { out.add(it) } }
fun <T> Array<T>.toFastList(): List<T> = FastArrayList<T>(this.size).also { out -> fastForEach { out.add(it) } }

inline fun <T> buildFastList(block: FastArrayList<T>.() -> Unit): FastArrayList<T> = FastArrayList<T>().apply(block)

fun <T> List<T>.toFastList(target: FastArrayList<T> = FastArrayList()): FastArrayList<T> {
    val minSize: Int = min(this.size, target.size)
    for (n in 0 until minSize) target[n] = this[n]
    for (n in minSize  until this.size) target.add(this[n])
    while (target.size > this.size) target.removeLast()
    return target
}

fun <T> FastArrayList<T>.toFastList(target: FastArrayList<T> = FastArrayList()): FastArrayList<T> {
    val source: FastArrayList<T> = this
    target.setAddAll(0, source)
    target.removeToSize(source.size)
    return target
}

interface MutableListEx<E> : MutableList<E> {
    fun removeRange(fromIndex: Int, toIndex: Int)

    fun addAll(elements: FastArrayList<E>): Boolean = addAll(elements as Collection<E>)
    fun setAddAll(index: Int, elements: FastArrayList<E>, offset: Int = 0, size: Int = elements.size - offset) {
        val setCount = min(size, this.size - index)
        setAll(index, elements, offset, setCount)
        addAll(elements, offset + setCount, size - setCount)
    }
    fun setAll(index: Int, elements: FastArrayList<E>, offset: Int = 0, size: Int = elements.size - offset) {
        for (n in 0 until size) this[index + n] = elements[offset + n]
    }
    fun addAll(elements: FastArrayList<E>, offset: Int = 0, size: Int = elements.size - offset) {
        for (n in 0 until size) this.add(elements[offset + n])
    }
    fun removeToSize(size: Int) {
        removeRange(size, this.size)
        //while (this.size > size) removeLast()
    }
}

private inline fun <T> List<T>.fastForEach(callback: (T) -> Unit) {
    var n = 0
    while (n < size) callback(this[n++])
}

private inline fun <T> Array<T>.fastForEach(callback: (T) -> Unit) {
    var n = 0
    while (n < size) callback(this[n++])
}
