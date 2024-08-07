package korlibs.datastructure

import korlibs.datastructure.ds.*
import korlibs.datastructure.internal.memory.Memory.arraycopy
import korlibs.datastructure.iterators.*
import korlibs.math.geom.*
import kotlin.math.*

interface IStackedLongArray2 : IStackedArray2<Long> {
    /** The [empty] value that will be returned if the specified cell it out of bounds, or empty */
    val empty: Long

    /** Duplicates the contents of this [IStackedLongArray2] keeping its contents data */
    fun clone(): IStackedLongArray2

    /** Sets the [value] at [x], [y] at [level], [startX] and [startY] are NOT used here so 0,0 means the top-left element */
    operator fun set(x: Int, y: Int, level: Int, value: Long)
    /** Gets the value at [x], [y] at [level], [startX] and [startY] are NOT used here so 0,0 means the top-left element */
    operator fun get(x: Int, y: Int, level: Int): Long

    /** Adds a new [value] on top of [x], [y] */
    fun push(x: Int, y: Int, value: Long) {
        set(x, y, getStackLevel(x, y), value)
    }

    /** Removes and returns the latest value on top of [x], [y] */
    fun pop(x: Int, y: Int): Long = getLast(x, y).also { removeLast(x, y) }

    /** Set the first [value] of a stack in the cell [x], [y] */
    fun setFirst(x: Int, y: Int, value: Long) {
        set(x, y, 0, value)
    }

    /** Gets the first value of the stack in the cell [x], [y] */
    fun getFirst(x: Int, y: Int): Long {
        val level = getStackLevel(x, y)
        if (level == 0) return empty
        return get(x, y, 0)
    }

    /** Gets the last value of the stack in the cell [x], [y] */
    fun getLast(x: Int, y: Int): Long {
        val level = getStackLevel(x, y)
        if (level == 0) return empty
        return get(x, y, level - 1)
    }

    override fun setToFrom(x0: Int, y0: Int, level0: Int, x1: Int, y1: Int, level1: Int) {
        this[x0, y0, level0] = this[x1, y1, level1]
    }
}

fun IStackedLongArray2.removeAt(p: PointInt, level: Int) = removeAt(p.x, p.y, level)
fun IStackedLongArray2.removeFirst(p: PointInt) = removeFirst(p.x, p.y)
fun IStackedLongArray2.removeLast(p: PointInt) = removeLast(p.x, p.y)
fun IStackedLongArray2.removeAll(p: PointInt) = removeAll(p.x, p.y)
fun IStackedLongArray2.getLast(p: PointInt): Long = getLast(p.x, p.y)
fun IStackedLongArray2.getStackLevel(p: PointInt): Int = getStackLevel(p.x, p.y)
fun IStackedLongArray2.get(p: PointInt, level: Int): Long = get(p.x, p.y, level)
fun IStackedLongArray2.set(p: PointInt, level: Int, value: Long) { set(p.x, p.y, level, value) }
fun IStackedLongArray2.push(p: PointInt, value: Long) { push(p.x, p.y, value) }
fun IStackedLongArray2.pop(p: PointInt) = pop(p.x, p.y)

/** Shortcut for [IStackedLongArray2.startX] + [IStackedLongArray2.width] */
val IStackedLongArray2.endX: Int get() = startX + width
/** Shortcut for [IStackedLongArray2.startY] + [IStackedLongArray2.height] */
val IStackedLongArray2.endY: Int get() = startY + height

class StackedLongArray2(
    override val width: Int,
    override val height: Int,
    override val empty: Long = EMPTY,
    override val startX: Int = 0,
    override val startY: Int = 0,
) : IStackedLongArray2 {
    override var contentVersion: Int = 0 ; private set

    override fun clone(): StackedLongArray2 {
        return StackedLongArray2(width, height, empty, startX, startY).also { out ->
            arraycopy(this.level.data, 0, out.level.data, 0, out.level.data.size)
            out.data.addAll(this.data.map { it.clone() })
        }
    }

    val level = IntArray2(width, height, 0)
    val data = fastArrayListOf<LongArray2>()

    override val maxLevel: Int get() = data.size

    companion object {
        const val EMPTY = -1L

        operator fun invoke(
            vararg layers: LongArray2,
            width: Int = layers.first().width,
            height: Int = layers.first().height,
            empty: Long = EMPTY,
            startX: Int = 0,
            startY: Int = 0,
        ): StackedLongArray2 {
            val stacked = StackedLongArray2(width, height, empty, startX = startX, startY = startY)
            stacked.level.fill { layers.size }
            stacked.data.addAll(layers)
            return stacked
        }
    }

    fun ensureLevel(level: Int) {
        while (level >= data.size) data.add(LongArray2(width, height, empty))
    }

    fun setLayer(level: Int, data: LongArray2) {
        ensureLevel(level)
        this.data[level] = data
        contentVersion++
    }

    override operator fun set(x: Int, y: Int, level: Int, value: Long) {
        ensureLevel(level)
        data[level][x, y] = value
        this.level[x, y] = maxOf(this.level[x, y], level + 1)
        contentVersion++
    }

    override operator fun get(x: Int, y: Int, level: Int): Long {
        if (level > this.level[x, y]) return empty
        return data[level][x, y]
    }

    override fun getStackLevel(x: Int, y: Int): Int {
        return this.level[x, y]
    }

    override fun IStackedArray2Base.Internal.setStackLevelInternal(x: Int, y: Int, levels: Int): Boolean {
        if (!inside(x, y)) return false
        this@StackedLongArray2.level[x, y] = levels
        return true
    }

    override fun removeLast(x: Int, y: Int): Boolean {
        level[x, y] = (level[x, y] - 1).coerceAtLeast(0)
        contentVersion++
        return true
    }
}

fun LongArray2.toStacked(): StackedLongArray2 = StackedLongArray2(this)

open class SparseChunkedStackedLongArray2(override var empty: Long = StackedLongArray2.EMPTY) : SparseChunkedStackedArray2<IStackedLongArray2>(), IStackedLongArray2 {
    constructor(vararg layers: IStackedLongArray2, empty: Long = StackedLongArray2.EMPTY) : this(empty) {
        layers.fastForEach { putChunk(it) }
    }

    override fun setEmptyFromChunk(chunk: IStackedLongArray2) {
        empty = chunk.empty
    }

    override fun set(x: Int, y: Int, level: Int, value: Long) {
        getChunkAt(x, y)?.let { chunk ->
            chunk[chunk.chunkX(x), chunk.chunkY(y), level] = value
            contentVersion++
        }
    }

    override fun get(x: Int, y: Int, level: Int): Long {
        getChunkAt(x, y)?.let { chunk ->
            return chunk[chunk.chunkX(x), chunk.chunkY(y), level]
        }
        return empty
    }

    override fun clone(): SparseChunkedStackedLongArray2 = SparseChunkedStackedLongArray2(empty).also { sparse ->
        findAllChunks().fastForEach {
            sparse.putChunk(it.clone())
        }
    }
}

class InfiniteGridStackedLongArray2(val grid: SizeInt = SizeInt(16, 16), override var empty: Long = StackedLongArray2.EMPTY) : SparseChunkedStackedLongArray2() {
    fun getGridXFor(x: Int) = idiv(x, grid.width)
    fun getGridYFor(y: Int) = idiv(y, grid.height)

    override fun getChunkAt(x: Int, y: Int, create: Boolean): IStackedLongArray2? {
        val gridX = getGridXFor(x)
        val gridY = getGridYFor(y)

        var res = super.getChunkAt(x, y, false)
        if (res == null && create) {
            res = putChunk(StackedLongArray2(grid.width, grid.height, empty = empty, startX = gridX * grid.width, startY = gridY * grid.height))
        }
        return res
    }
}
