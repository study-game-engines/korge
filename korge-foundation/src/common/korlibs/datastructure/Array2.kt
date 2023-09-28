@file:Suppress("DuplicatedCode")

package korlibs.datastructure

inline fun <TGen : Any, RGen : Any> IArray2<TGen>.map2(gen: (x: Int, y: Int, v: TGen) -> RGen) =
    Array2<RGen>(width, height) {
        val x = it % width
        val y = it / width
        gen(x, y, this.getAt(x, y))
    }

inline fun IntArray2.map2(gen: (x: Int, y: Int, v: Int) -> Int): IntArray2 =
    IntArray2(width, height) {
        val x = it % width
        val y = it / width
        gen(x, y, this[x, y])
    }

inline fun FloatArray2.map2(gen: (x: Int, y: Int, v: Float) -> Float): FloatArray2 =
    FloatArray2(width, height) {
        val x = it % width
        val y = it / width
        gen(x, y, this[x, y])
    }

inline fun DoubleArray2.map2(gen: (x: Int, y: Int, v: Double) -> Double): DoubleArray2 =
    DoubleArray2(width, height) {
        val x = it % width
        val y = it / width
        gen(x, y, this[x, y])
    }

// typealias BitIArray2 = IArray2<Bit>
typealias BooleanIArray2 = IArray2<Boolean>
typealias ByteIArray2 = IArray2<Byte>
typealias ShortIArray2 = IArray2<Short>
typealias CharIArray2 = IArray2<Char>
typealias IntIArray2 = IArray2<Int>
typealias LongIArray2 = IArray2<Long>
typealias FloatIArray2 = IArray2<Float>
typealias DoubleIArray2 = IArray2<Double>

// @NOTE: AUTOGENERATED: ONLY MODIFY FROM  GENERIC TEMPLATE to END OF GENERIC TEMPLATE
// Then use ./gradlew generate to regenerate the rest of the file.

// GENERIC TEMPLATE //////////////////////////////////////////

@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class Array2<TGen>(override val width: Int, override val height: Int, val data: Array<TGen>) : IArray2<TGen> {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun <TGen : Any> invoke(width: Int, height: Int, fill: TGen): Array2<TGen> =
            Array2<TGen>(width, height, Array<Any>(width * height) { fill } as Array<TGen>)

        inline operator fun <TGen : Any> invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> TGen
        ): Array2<TGen> =
            Array2<TGen>(width, height, Array<Any>(width * height) { gen(it) } as Array<TGen>)

        inline fun <TGen : Any> withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> TGen
        ): Array2<TGen> =
            Array2<TGen>(
                width,
                height,
                Array<Any>(width * height) { gen(it % width, it / width) } as Array<TGen>)

        inline operator fun <TGen : Any> invoke(rows: List<List<TGen>>): Array2<TGen> {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (Array2<TGen>(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun <TGen : Any> invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> TGen
        ): Array2<TGen> {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return Array2<TGen>(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun <TGen : Any> invoke(
            map: String,
            default: TGen,
            transform: Map<Char, TGen>
        ): Array2<TGen> {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun <TGen : Any> fromString(
            maps: Map<Char, TGen>,
            default: TGen,
            code: String,
            marginChar: Char = '\u0000'
        ): Array2<TGen> {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: TGen) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: TGen): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): TGen = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is Array2<*/*TGen*/>) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): TGen = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: TGen) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): TGen? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: TGen) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = Array2<TGen>(width, height, data.copyOf())

    override fun iterator(): Iterator<TGen> = data.iterator()

    override fun toString(): String = asString()
}

// END OF GENERIC TEMPLATE ///////////////////////////////////

// AUTOGENERATED: DO NOT MODIFY MANUALLY STARTING FROM HERE!

// Int


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class IntArray2(override val width: Int, override val height: Int, val data: IntArray) : IntIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Int): IntArray2 =
            IntArray2(width, height, IntArray(width * height) { fill } as IntArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Int
        ): IntArray2 =
            IntArray2(width, height, IntArray(width * height) { gen(it) } as IntArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Int
        ): IntArray2 =
            IntArray2(
                width,
                height,
                IntArray(width * height) { gen(it % width, it / width) } as IntArray)

        inline operator fun  invoke(rows: List<List<Int>>): IntArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (IntArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Int
        ): IntArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return IntArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Int,
            transform: Map<Char, Int>
        ): IntArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Int>,
            default: Int,
            code: String,
            marginChar: Char = '\u0000'
        ): IntArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Int) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Int): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Int = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is IntArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Int = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Int) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Int? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Int) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = IntArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Int> = data.iterator()

    override fun toString(): String = asString()
}



// Double


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class DoubleArray2(override val width: Int, override val height: Int, val data: DoubleArray) : DoubleIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Double): DoubleArray2 =
            DoubleArray2(width, height, DoubleArray(width * height) { fill } as DoubleArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Double
        ): DoubleArray2 =
            DoubleArray2(width, height, DoubleArray(width * height) { gen(it) } as DoubleArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Double
        ): DoubleArray2 =
            DoubleArray2(
                width,
                height,
                DoubleArray(width * height) { gen(it % width, it / width) } as DoubleArray)

        inline operator fun  invoke(rows: List<List<Double>>): DoubleArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (DoubleArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Double
        ): DoubleArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return DoubleArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Double,
            transform: Map<Char, Double>
        ): DoubleArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Double>,
            default: Double,
            code: String,
            marginChar: Char = '\u0000'
        ): DoubleArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Double) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Double): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Double = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is DoubleArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Double = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Double) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Double? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Double) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = DoubleArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Double> = data.iterator()

    override fun toString(): String = asString()
}



// Float


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class FloatArray2(override val width: Int, override val height: Int, val data: FloatArray) : FloatIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Float): FloatArray2 =
            FloatArray2(width, height, FloatArray(width * height) { fill } as FloatArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Float
        ): FloatArray2 =
            FloatArray2(width, height, FloatArray(width * height) { gen(it) } as FloatArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Float
        ): FloatArray2 =
            FloatArray2(
                width,
                height,
                FloatArray(width * height) { gen(it % width, it / width) } as FloatArray)

        inline operator fun  invoke(rows: List<List<Float>>): FloatArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (FloatArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Float
        ): FloatArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return FloatArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Float,
            transform: Map<Char, Float>
        ): FloatArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Float>,
            default: Float,
            code: String,
            marginChar: Char = '\u0000'
        ): FloatArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Float) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Float): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Float = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is FloatArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Float = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Float) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Float? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Float) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = FloatArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Float> = data.iterator()

    override fun toString(): String = asString()
}



// Byte


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class ByteArray2(override val width: Int, override val height: Int, val data: ByteArray) : ByteIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Byte): ByteArray2 =
            ByteArray2(width, height, ByteArray(width * height) { fill } as ByteArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Byte
        ): ByteArray2 =
            ByteArray2(width, height, ByteArray(width * height) { gen(it) } as ByteArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Byte
        ): ByteArray2 =
            ByteArray2(
                width,
                height,
                ByteArray(width * height) { gen(it % width, it / width) } as ByteArray)

        inline operator fun  invoke(rows: List<List<Byte>>): ByteArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (ByteArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Byte
        ): ByteArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return ByteArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Byte,
            transform: Map<Char, Byte>
        ): ByteArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Byte>,
            default: Byte,
            code: String,
            marginChar: Char = '\u0000'
        ): ByteArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Byte) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Byte): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Byte = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is ByteArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Byte = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Byte) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Byte? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Byte) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = ByteArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Byte> = data.iterator()

    override fun toString(): String = asString()
}



// Char


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class CharArray2(override val width: Int, override val height: Int, val data: CharArray) : CharIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Char): CharArray2 =
            CharArray2(width, height, CharArray(width * height) { fill } as CharArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Char
        ): CharArray2 =
            CharArray2(width, height, CharArray(width * height) { gen(it) } as CharArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Char
        ): CharArray2 =
            CharArray2(
                width,
                height,
                CharArray(width * height) { gen(it % width, it / width) } as CharArray)

        inline operator fun  invoke(rows: List<List<Char>>): CharArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (CharArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Char
        ): CharArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return CharArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Char,
            transform: Map<Char, Char>
        ): CharArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Char>,
            default: Char,
            code: String,
            marginChar: Char = '\u0000'
        ): CharArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Char) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Char): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Char = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is CharArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Char = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Char) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Char? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Char) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = CharArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Char> = data.iterator()

    override fun toString(): String = asString()
}



// Short


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class ShortArray2(override val width: Int, override val height: Int, val data: ShortArray) : ShortIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Short): ShortArray2 =
            ShortArray2(width, height, ShortArray(width * height) { fill } as ShortArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Short
        ): ShortArray2 =
            ShortArray2(width, height, ShortArray(width * height) { gen(it) } as ShortArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Short
        ): ShortArray2 =
            ShortArray2(
                width,
                height,
                ShortArray(width * height) { gen(it % width, it / width) } as ShortArray)

        inline operator fun  invoke(rows: List<List<Short>>): ShortArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (ShortArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Short
        ): ShortArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return ShortArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Short,
            transform: Map<Char, Short>
        ): ShortArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Short>,
            default: Short,
            code: String,
            marginChar: Char = '\u0000'
        ): ShortArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Short) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Short): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Short = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is ShortArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Short = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Short) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Short? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Short) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = ShortArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Short> = data.iterator()

    override fun toString(): String = asString()
}



// Long


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class LongArray2(override val width: Int, override val height: Int, val data: LongArray) : LongIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Long): LongArray2 =
            LongArray2(width, height, LongArray(width * height) { fill } as LongArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Long
        ): LongArray2 =
            LongArray2(width, height, LongArray(width * height) { gen(it) } as LongArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Long
        ): LongArray2 =
            LongArray2(
                width,
                height,
                LongArray(width * height) { gen(it % width, it / width) } as LongArray)

        inline operator fun  invoke(rows: List<List<Long>>): LongArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (LongArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Long
        ): LongArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return LongArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Long,
            transform: Map<Char, Long>
        ): LongArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Long>,
            default: Long,
            code: String,
            marginChar: Char = '\u0000'
        ): LongArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Long) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Long): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Long = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is LongArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Long = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Long) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Long? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Long) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = LongArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Long> = data.iterator()

    override fun toString(): String = asString()
}



// Boolean


@Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")
data class BooleanArray2(override val width: Int, override val height: Int, val data: BooleanArray) : BooleanIArray2 {
    init {
        IArray2.checkArraySize(width, height, data.size)
    }
    companion object {
        inline operator fun  invoke(width: Int, height: Int, fill: Boolean): BooleanArray2 =
            BooleanArray2(width, height, BooleanArray(width * height) { fill } as BooleanArray)

        inline operator fun  invoke(
            width: Int,
            height: Int,
            gen: (n: Int) -> Boolean
        ): BooleanArray2 =
            BooleanArray2(width, height, BooleanArray(width * height) { gen(it) } as BooleanArray)

        inline fun  withGen(
            width: Int,
            height: Int,
            gen: (x: Int, y: Int) -> Boolean
        ): BooleanArray2 =
            BooleanArray2(
                width,
                height,
                BooleanArray(width * height) { gen(it % width, it / width) } as BooleanArray)

        inline operator fun  invoke(rows: List<List<Boolean>>): BooleanArray2 {
            val width = rows[0].size
            val height = rows.size
            val anyCell = rows[0][0]
            return (BooleanArray2(width, height) { anyCell }).apply { set(rows) }
        }

        inline operator fun  invoke(
            map: String,
            marginChar: Char = '\u0000',
            gen: (char: Char, x: Int, y: Int) -> Boolean
        ): BooleanArray2 {
            val lines = map.lines()
                .map {
                    val res = it.trim()
                    if (res.startsWith(marginChar)) {
                        res.substring(0, res.length)
                    } else {
                        res
                    }
                }
                .filter { it.isNotEmpty() }
            val width = lines.map { it.length }.maxOrNull() ?: 0
            val height = lines.size

            return BooleanArray2(width, height) { n ->
                val x = n % width
                val y = n / width
                gen(lines.getOrNull(y)?.getOrNull(x) ?: ' ', x, y)
            }
        }

        inline operator fun  invoke(
            map: String,
            default: Boolean,
            transform: Map<Char, Boolean>
        ): BooleanArray2 {
            return invoke(map) { c, _, _ -> transform[c] ?: default }
        }

        inline fun  fromString(
            maps: Map<Char, Boolean>,
            default: Boolean,
            code: String,
            marginChar: Char = '\u0000'
        ): BooleanArray2 {
            return invoke(code, marginChar = marginChar) { c, _, _ -> maps[c] ?: default }
        }
    }

    override fun setAt(idx: Int, value: Boolean) {
        this.data[idx] = value
    }

    override fun printAt(idx: Int) {
        print(this.data[idx])
    }

    override fun equalsAt(idx: Int, value: Boolean): Boolean {
        return this.data[idx]?.equals(value) ?: false
    }

    override fun getAt(idx: Int): Boolean = this.data[idx]

    override fun equals(other: Any?): Boolean {
        return (other is BooleanArray2) && this.width == other.width && this.height == other.height && this.data.contentEquals(
            other.data
        )
    }

    operator fun get(x: Int, y: Int): Boolean = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Boolean) {
        data[index(x, y)] = value
    }

    fun tryGet(x: Int, y: Int): Boolean? = if (inside(x, y)) data[index(x, y)] else null
    fun trySet(x: Int, y: Int, value: Boolean) {
        if (inside(x, y)) data[index(x, y)] = value
    }

    override fun hashCode(): Int = width + height + data.contentHashCode()

    fun clone() = BooleanArray2(width, height, data.copyOf())

    override fun iterator(): Iterator<Boolean> = data.iterator()

    override fun toString(): String = asString()
}