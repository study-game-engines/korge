@file:Suppress("PackageDirectoryMismatch")

package korlibs.io.serialization.json

import korlibs.datastructure.*
import korlibs.io.util.*
import korlibs.util.*
import kotlin.collections.set

object JsonFast : Json() {
    override val optimizeNumbers: Boolean = true
    override fun createDoubleArrayList(doubles: MiniNumberArrayList): Any = doubles
}

open class Json {
    companion object : Json() { }

    fun parse(s: String): Any? = parse(SimpleStrReader(s))

    fun stringify(obj: Any?, pretty: Boolean = false): String = when {
        pretty -> SimpleIndenter().apply { stringifyPretty(obj, this) }.toString()
        else -> StringBuilder().apply { stringify(obj, this) }.toString()
    }

    protected open val optimizeNumbers: Boolean = false
    //protected open val optimizeNumbers: Boolean = true
    protected open fun <T> createArrayList(capacity: Int = 16): MutableList<T> = ArrayList(capacity)
    protected open fun createDoubleArrayList(doubles: MiniNumberArrayList): Any = doubles.toDoubleArray().toList()
    //protected open fun createDoubleArrayList(doubles: MiniNumberArrayList): Any = doubles

    protected class MiniNumberArrayList : DoubleList {
        override var size: Int = 0
            private set

        @PublishedApi internal var items = DoubleArray(16)
        val capacity get() = items.size

        fun clear() {
            size = 0
        }

        override operator fun get(index: Int): Double = items[index]

        fun add(value: Double) {
            if (size >= capacity) {
                items = items.copyOf(items.size * 3)
            }
            items[size++] = value
        }

        inline fun fastForEach(block: (Double) -> Unit) {
            for (n in 0 until size) block(items[n])
        }

        override fun toDoubleArray(): DoubleArray = items.copyOf(size)
        override fun clone(): DoubleList = MiniNumberArrayList().also {
            it.size = size
            it.items = items.copyOf()
        }

        override fun equals(other: Any?): Boolean {
            if (other !is Collection<*>) return false
            if (other.size != this.size) return false
            if (other is DoubleList) {
                for (n in 0 until size) if (this[n] != other[n]) return false
                return true
            }
            if (other is List<*>) {
                for (n in 0 until size) if (this[n] != other[n]) return false
                return true
            }
            var n = 0
            for (v in other) if (this[n++] != v) return false
            return true
        }

        override fun hashCode(): Int = this.items.contentHashCode(0, size)

        private inline fun hashCoder(count: Int, gen: (index: Int) -> Int): Int {
            var out = 0
            for (n in 0 until count) {
                out *= 7
                out += gen(n)
            }
            return out
        }
        private fun DoubleArray.contentHashCode(src: Int, dst: Int): Int = hashCoder(dst - src) { this[src + it].toInt() } // Do not want to use Long (.toRawBits) to prevent boxing on JS

        override fun toString(): String = StringBuilder(2 + 5 * size).also { sb ->
            sb.append('[')
            for (n in 0 until size) {
                if (n != 0) sb.append(", ")
                val v = this.getAt(n)
                if (v.toInt().toDouble() == v) sb.append(v.toInt()) else sb.append(v)
            }
            sb.append(']')
        }.toString()
    }

    interface CustomSerializer {
        fun encodeToJson(b: StringBuilder)
    }

    fun parse(s: SimpleStrReader): Any? = when (val ic = s.skipSpaces().peekChar()) {
        '{' -> parseObject(s)
        '[' -> parseArray(s)
        //'-', '+', in '0'..'9' -> { // @TODO: Kotlin native doesn't optimize char ranges
        '-', '+', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
            val dres = parseNumber(s)
            if (dres.toInt().toDouble() == dres) dres.toInt() else dres
        }
        't' -> true.also { s.expect("true") }
        'f' -> false.also { s.expect("false") }
        'n' -> null.also { s.expect("null") }
        'u' -> null.also { s.expect("undefined") }
        '"' -> s.readStringLit().toString()
        else -> invalidJson("Not expected '$ic' in $s")
    }

    private fun parseObject(s: SimpleStrReader): Map<String, Any?> {
        s.skipExpect('{')
        return LinkedHashMap<String, Any?>().apply {
            obj@ while (true) {
                when (s.skipSpaces().peekChar()) {
                    '}' -> { s.readChar(); break@obj }
                    ',' -> { s.readChar(); continue@obj }
                    else -> Unit
                }
                val key = parse(s) as String
                s.skipSpaces().skipExpect(':')
                val value = parse(s)
                this[key] = value
            }
        }
    }

    private fun parseArray(s: SimpleStrReader): Any {
        var out: MutableList<Any?>? = null
        var outNumber: MiniNumberArrayList? = null
        s.skipExpect('[')
        array@ while (true) {
            when (s.skipSpaces().peekChar()) {
                ']' -> { s.readChar(); break@array }
                ',' -> { s.readChar(); continue@array }
                else -> Unit
            }
            val v = s.peekChar()
            if (out == null && optimizeNumbers && (v in '0'..'9' || v == '-')) {
                if (outNumber == null) {
                    outNumber = MiniNumberArrayList()
                }
                outNumber.add(parseNumber(s))
            } else {
                if (out == null) out = createArrayList(outNumber?.size ?: 16)
                if (outNumber != null) {
                    outNumber.fastForEach { out.add(it) }
                    outNumber = null
                }
                out.add(parse(s))
            }
        }
        return outNumber?.let { createDoubleArrayList(outNumber) } ?: out ?: createArrayList<Any?>()
    }

    private fun parseNumber(s: SimpleStrReader): Double = NumberParser.parseDouble {
        val c = s.peekChar()
        val isC = ((c >= '0') && (c <= '9')) || c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+'
        if (isC) s.readChar()
        if (isC) c else '\u0000'
    }

    fun stringify(obj: Any?, b: StringBuilder) {
        when (obj) {
            null -> b.append("null")
            is Boolean -> b.append(if (obj) "true" else "false")
            is Map<*, *> -> {
                b.append('{')
                for ((i, v) in obj.entries.withIndex()) {
                    if (i != 0) b.append(',')
                    stringify(v.key, b)
                    b.append(':')
                    stringify(v.value, b)
                }
                b.append('}')
            }
            is Iterable<*> -> {
                b.append('[')
                for ((i, v) in obj.withIndex()) {
                    if (i != 0) b.append(',')
                    stringify(v, b)
                }
                b.append(']')
            }
            is Enum<*> -> encodeString(obj.name, b)
            is String -> encodeString(obj, b)
            is Number -> b.append("$obj")
            is CustomSerializer -> obj.encodeToJson(b)
            else -> throw IllegalArgumentException("Don't know how to serialize $obj") //encode(ClassFactory(obj::class).toMap(obj), b)
        }
    }

    fun stringifyPretty(obj: Any?, b: SimpleIndenter) {
        when (obj) {
            null -> b.inline("null")
            is Boolean -> b.inline(if (obj) "true" else "false")
            is Map<*, *> -> {
                b.line("{")
                b.indent {
                    val entries = obj.entries
                    for ((i, v) in entries.withIndex()) {
                        if (i != 0) b.line(",")
                        b.inline(encodeString("" + v.key))
                        b.inline(": ")
                        stringifyPretty(v.value, b)
                        if (i == entries.size - 1) b.line("")
                    }
                }
                b.inline("}")
            }
            is Iterable<*> -> {
                b.line("[")
                b.indent {
                    val entries = obj.toList()
                    for ((i, v) in entries.withIndex()) {
                        if (i != 0) b.line(",")
                        stringifyPretty(v, b)
                        if (i == entries.size - 1) b.line("")
                    }
                }
                b.inline("]")
            }
            is String -> b.inline(encodeString(obj))
            is Number -> b.inline("$obj")
            is CustomSerializer -> b.inline(StringBuilder().apply { obj.encodeToJson(this) }.toString())
            else -> {
                throw IllegalArgumentException("Don't know how to serialize $obj")
                //encode(ClassFactory(obj::class).toMap(obj), b)
            }
        }
    }

    private fun encodeString(str: String) = StringBuilder().apply { encodeString(str, this) }.toString()

    private fun encodeString(str: String, b: StringBuilder) {
        b.append('"')
        for (c in str) {
            when (c) {
                '\\' -> b.append("\\\\"); '/' -> b.append("\\/"); '\'' -> b.append("\\'")
                '"' -> b.append("\\\""); '\b' -> b.append("\\b"); '\u000c' -> b.append("\\f")
                '\n' -> b.append("\\n"); '\r' -> b.append("\\r"); '\t' -> b.append("\\t")
                else -> b.append(c)
            }
        }
        b.append('"')
    }

    private fun invalidJson(msg: String = "Invalid JSON"): Nothing = throw IllegalArgumentException(msg)

    fun SimpleStrReader.readStringLit(reportErrors: Boolean = true, out: StringBuilder = StringBuilder()): StringBuilder {
        val quotec = readChar()
        when (quotec) {
            '"', '\'' -> Unit
            else -> throw IllegalArgumentException("Invalid string literal")
        }
        var closed = false
        loop@ while (hasMore) {
            when (val c = readChar()) {
                '\\' -> {
                    val cc = readChar()
                    val c: Char = when (cc) {
                        '\\' -> '\\'; '/' -> '/'; '\'' -> '\''; '"' -> '"'
                        'b' -> '\b'; 'f' -> '\u000c'; 'n' -> '\n'; 'r' -> '\r'; 't' -> '\t'
                        'u' -> NumberParser.parseInt(radix = 16) { if (it >= 4) NumberParser.END else readChar() }.toChar()
                        else -> throw IllegalArgumentException("Invalid char '$cc'")
                    }
                    out.append(c)
                }
                quotec -> {
                    closed = true
                    break@loop
                }
                else -> out.append(c)
            }
        }
        if (!closed && reportErrors) {
            throw RuntimeException("String literal not closed! '${this}'")
        }
        return out
    }

    private fun SimpleStrReader.expect(str: String) {
        for (n in str.indices) {
            val c = readChar()
            if (c != str[n]) throw IllegalStateException("Expected '$str' but found '$c' at $n")
        }
    }

    private fun SimpleStrReader.skipSpaces(): SimpleStrReader {
        this.skipWhile { it.isWhitespaceFast() }
        return this
    }

    private inline fun SimpleStrReader.skipWhile(filter: (Char) -> Boolean) {
        while (hasMore && filter(this.peekChar())) {
            this.readChar()
        }
    }

    private fun SimpleStrReader.skipExpect(expected: Char) {
        val readed = this.readChar()
        if (readed != expected) throw IllegalArgumentException("Expected '$expected' but found '$readed' at $pos")
    }

    private fun Char.isWhitespaceFast(): Boolean = this == ' ' || this == '\t' || this == '\r' || this == '\n'
}

fun String.fromJson(): Any? = Json.parse(this)
fun Map<*, *>.toJson(pretty: Boolean = false): String = Json.stringify(this, pretty)
