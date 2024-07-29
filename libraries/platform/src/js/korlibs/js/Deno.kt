package korlibs.js

import org.khronos.webgl.*
import kotlin.js.*

val Deno_isDeno: Boolean get() = js("('Deno' in window)").unsafeCast<Boolean>()

external object Deno {
    // Sync
    fun realPathSync(path: String): String
    fun readLinkSync(path: String): String?
    fun linkSync(oldpath: String?, newpath: String?): String?
    fun openSync(path: String, options: dynamic): DenoFsFile
    fun readDirSync(path: String): JSIterable<DenoDirEntry>
    fun mkdirSync(path: String, options: dynamic = definedExternally)
    fun removeSync(path: String, options: dynamic = definedExternally)
    fun statSync(path: String): DenoFileInfo
    fun writeFileSync(path: String, data: ByteArray, options: dynamic = definedExternally)
    fun readFileSync(path: String): ByteArray

    // Async
    fun realPath(path: String): Promise<String>
    fun readLink(path: String): Promise<String?>
    fun open(path: String, options: dynamic = definedExternally): Promise<DenoFsFile>
    fun readDir(path: String): JSAsyncIterable<DenoDirEntry>
    fun mkdir(path: String, options: dynamic = definedExternally): Promise<Unit>
    fun remove(path: String, options: dynamic = definedExternally): Promise<Unit>
    fun rename(oldPath: String, newPath: String): Promise<Unit>
    fun stat(path: String): Promise<DenoFileInfo>

    // Misc
    fun inspect(value: dynamic): String
    fun exit(exitCode: Int = definedExternally)
    fun cwd(): String
    fun <T> dlopen(path: String, symbols: dynamic = definedExternally): DenoDlOpen<T>
    fun addSignalListener(s: String, function: () -> Unit)

    object SeekMode {
        val Start: Int // 0
        val Current: Int // 1
        val End: Int // 2
    }

    val mainModule: String
    val build: DenoBuild

    val env: dynamic

    object UnsafePointer {
        fun create(value: JsBigInt): Deno_PointerObject
        fun equals(a: JsBigInt, b: JsBigInt): Boolean
        fun of(a: ArrayBufferView): Deno_PointerObject
        fun offset(a: ArrayBufferView, offset: Int): Deno_PointerObject?
        fun value(value: Deno_PointerObject?): dynamic
    }

    class UnsafePointerView {
        constructor(pointer: Deno_PointerObject)
        val pointer: Deno_PointerObject

        fun getInt8(offset: Int = definedExternally): Byte
        fun getInt16(offset: Int = definedExternally): Short
        fun getInt32(offset: Int = definedExternally): Int
        fun getBigInt64(offset: Int = definedExternally): JsBigInt
        fun getFloat32(offset: Int = definedExternally): Float
        fun getFloat64(offset: Int = definedExternally): Double

        fun getArrayBuffer(byteLength: Int, offset: Int = definedExternally): ArrayBuffer

        companion object {
            fun getArrayBuffer(pointer: Deno_PointerObject, byteLength: Int, offset: Int = definedExternally): ArrayBuffer
            fun getCString(pointer: Deno_PointerObject, offset: Int = definedExternally): String
        }
    }

    class UnsafeFnPointer(pointer: DenoPointer?, definition: dynamic) {
        val pointer: DenoPointer
    }
}

external interface DenoFileInfo {
    val isFile: Boolean
    val isDirectory: Boolean
    val isSymlink: Boolean
    val size: Double
    val mtime: Date?
    val atime: Date?
    val birthtime: Date?
    val dev: Double
    val ino: Double?
    val mode: Double?
    val nlink: Double?
    val uid: Double?
    val gid: Double?
    val rdev: Double?
    val blksize: Double?
    val blocks: Double?
    val isBlockDEvice: Double?
    val isFifo: Double?
    val isSocket: Double?
}

external interface DenoFsFile {
    // Both
    fun close()

    // Sync
    fun truncateSync(len: Double? = definedExternally): Unit
    fun seekSync(pos: Double, whence: Int): Double
    fun writeSync(data: Uint8Array): Double
    fun readSync(data: Uint8Array): Double?
    fun statSync(): DenoFileInfo

    // Async
    fun truncate(len: Double? = definedExternally): Promise<Unit>
    fun seek(pos: Double, whence: Int): Promise<Double>
    fun write(data: Uint8Array): Promise<Double>
    fun read(data: Uint8Array): Promise<Double?>
    fun stat(): Promise<DenoFileInfo>
}

external interface DenoDlOpen<T> {
    val symbols: T
}

external interface DenoBuild {
    /** aarch64-apple-darwin */
    val target: String
    /** x86_64, aarch64 */
    val arch: String
    /** darwin, linux, windows, freebsd, netbsd, aix, solaris, illumos */
    val os: String
    /** apple */
    val vendor: String
    //{
    //    target: "aarch64-apple-darwin",
    //    arch: "aarch64",
    //    os: "darwin",
    //    vendor: "apple",
    //    env: undefined
    //}
}

external interface DenoDirEntry {
    val name: String
    val isFile: Boolean
    val isDirectory: Boolean
    val isSymlink: Boolean
}

external val Object: dynamic

//external interface Deno_PointerValue<T>
external interface Deno_PointerObject
external interface Deno_PointerValue<T> : Deno_PointerObject

val Deno_PointerObject.pointer get() = DenoPointer(this)

//@JsName("Deno")
data class DenoPointer(val rawPointer: Deno_PointerObject) {
    companion object {
        fun wrap(any: Deno_PointerObject): DenoPointer {
            return DenoPointer(any)
        }

        fun isRawPointer(any: dynamic): Boolean {
            if (any === null || any === undefined) return false
            return Object.getPrototypeOf(any) == null
        }
    }
}
//class DenoPointer

val Deno_PointerObject.value: JsBigInt get() = Deno.UnsafePointer.value(this)

fun Deno_PointerObject.readStringz(offset: Int = 0): String {
    return Deno.UnsafePointerView.getCString(this, offset)
}
