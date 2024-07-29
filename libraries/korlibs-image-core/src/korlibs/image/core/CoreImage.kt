package korlibs.image.core

import kotlin.math.*

/**
 * An image representation that can be used to encode/decode images in different formats.
 */
interface CoreImage {
    /** [width] of the image */
    val width: Int
    /** [height] of the image */
    val height: Int
    /** Native object. Like IntArray for CoreImage32 or BufferedImage */
    val native: Any
    /** Bits per pixel */
    val bpp: Int
    /** Determine if the pixels are premultiplied by its alpha */
    val premultiplied: Boolean
    /** Returns a [CoreImage32] image */
    fun to32(): CoreImage32

    companion object
}

/**
 * A 32-bit image representation.
 *
 * [data] is in [CoreImage32Color] format
 */
class CoreImage32(
    override val width: Int,
    override val height: Int,
    val data: IntArray = IntArray(width * height),
    override val premultiplied: Boolean = true
) : CoreImage {
    override val native get() = data
    override val bpp: Int = 32
    override fun to32(): CoreImage32 = this
}

fun CoreImage32.premultiplied(): CoreImage32 {
    if (premultiplied) return this
    return CoreImage32(width, height, IntArray(data.size) { CoreImage32Color(data[it]).premultiplied().value }, premultiplied = true)
}

fun CoreImage32.depremultiplied(): CoreImage32 {
    if (!premultiplied) return this
    return CoreImage32(width, height, IntArray(data.size) { CoreImage32Color(data[it]).depremultiplied().value }, premultiplied = false)
}

/**
 * 32-bit RGBA color format. Used in [CoreImage32]
 */
inline class CoreImage32Color(val value: Int) {
    constructor(red: UByte, green: UByte, blue: UByte, alpha: UByte = 255u) : this((red.toInt() shl RED_OFFSET) or (green.toInt() shl GREEN_OFFSET) or (blue.toInt() shl BLUE_OFFSET) or (alpha.toInt() shl ALPHA_OFFSET))
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(red.coerceIn(0, 255).toUByte(), green.coerceIn(0, 255).toUByte(), blue.coerceIn(0, 255).toUByte(), alpha.coerceIn(0, 255).toUByte())

    val red: Int get() = (value ushr RED_OFFSET) and 0xFF
    val green: Int get() = (value ushr GREEN_OFFSET) and 0xFF
    val blue: Int get() = (value ushr BLUE_OFFSET) and 0xFF
    val alpha: Int get() = (value ushr ALPHA_OFFSET) and 0xFF

    fun premultiplied(): CoreImage32Color {
        val Af = alpha / 255f
        val R = (red * Af).roundToInt()
        val G = (green * Af).roundToInt()
        val B = (blue * Af).roundToInt()
        return CoreImage32Color(R, G, B, alpha)
    }

    fun depremultiplied(): CoreImage32Color {
        val alpha = alpha
        return when (alpha) {
            0 -> CoreImage32Color(0)
            else -> {
                val iAf = 255f / alpha
                CoreImage32Color((red * iAf).roundToInt(), (green * iAf).roundToInt(), (blue * iAf).roundToInt(), alpha)
            }
        }

    }

    companion object {
        const val RED_OFFSET = 0
        const val GREEN_OFFSET = 8
        const val BLUE_OFFSET = 16
        const val ALPHA_OFFSET = 24
    }
}


internal fun CoreImage32Color.toHexString(): String = buildString(9) {
    val HEX = "0123456789ABCDEF"
    append("#")
    append(HEX[(red ushr 4) and 0xF])
    append(HEX[(red ushr 0) and 0xF])
    append(HEX[(green ushr 4) and 0xF])
    append(HEX[(green ushr 0) and 0xF])
    append(HEX[(blue ushr 4) and 0xF])
    append(HEX[(blue ushr 0) and 0xF])
    append(HEX[(alpha ushr 4) and 0xF])
    append(HEX[(alpha ushr 0) and 0xF])
}

/**
 * Provides information about an image.
 */
data class CoreImageInfo(
    /** [width] of the image */
    val width: Int,
    /** [height] of the image */
    val height: Int,
    /** Bits per pixel */
    val bpp: Int = 32,
    val format: CoreImageFormat? = null,
    val premultiplied: Boolean = true,
)

/**
 * Provides information about an image.
 */
fun CoreImage.info(): CoreImageInfo = CoreImageInfo(width, height, bpp, format = null, premultiplied = premultiplied)

/**
 * Image format: [PNG], [JPEG], [WEBP], [AVIF], etc.
 */
inline class CoreImageFormat(val name: String) {
    companion object {
        val PNG = CoreImageFormat("png")
        val JPEG = CoreImageFormat("jpeg")
        val GIF = CoreImageFormat("gif")
        val TGA = CoreImageFormat("tga")
        val WEBP = CoreImageFormat("webp")
        val AVIF = CoreImageFormat("avif")

        fun fromMimeType(mimeType: String): CoreImageFormat = CoreImageFormat(mimeType.substringAfterLast('/').lowercase())
    }
}

/**
 * Provides image encoding/decoding capabilities.
 */
interface CoreImageFormatProvider {
    /**
     * Whether this provider is valid or not.
     */
    val isSupported: Boolean get() = true

    /**
     * Gets the [CoreImageInfo] of a [data] ByteArray. Potentially without decoding the pixels.
     */
    suspend fun info(data: ByteArray): CoreImageInfo = decode(data).let {
        CoreImageInfo(width = it.width, height = it.height, bpp = it.bpp, format = null)
    }
    /**
     * Decodes a [data] ByteArray into a CoreImage
     */
    suspend fun decode(data: ByteArray): CoreImage
    /**
     * Encodes a [CoreImage] into a [ByteArray] in the specified [format] (PNG, JPEG, etc.)
     */
    suspend fun encode(image: CoreImage, format: CoreImageFormat, level: Double = 1.0): ByteArray

    companion object
}

object DummyCoreImageFormatProvider : CoreImageFormatProvider {
    override val isSupported: Boolean get() = false

    override suspend fun decode(data: ByteArray): CoreImage {
        TODO("Not yet implemented")
    }

    override suspend fun encode(image: CoreImage, format: CoreImageFormat, level: Double): ByteArray {
        TODO("Not yet implemented")
    }
}

expect val CoreImageFormatProvider_default: CoreImageFormatProvider

private var _CoreImageFormatProvider_current: CoreImageFormatProvider? = null

/**
 * Current [CoreImageFormatProvider] used by [CoreImage] operations. By default, it uses the default one from the platform.
 */
var CoreImageFormatProvider.Companion.CURRENT: CoreImageFormatProvider
    get() = _CoreImageFormatProvider_current ?: CoreImageFormatProvider_default
    set(value) { _CoreImageFormatProvider_current = value }

/**
 * Gets the [CoreImageInfo] of a [data] ByteArray. Potentially without decoding the pixels.
 */
suspend fun CoreImage.Companion.info(data: ByteArray): CoreImageInfo =
    CoreImageFormatProvider.CURRENT.info(data)

/**
 * Decodes a [data] ByteArray into a CoreImage
 */
suspend fun CoreImage.Companion.decodeBytes(data: ByteArray): CoreImage =
    CoreImageFormatProvider.CURRENT.decode(data)

/**
 * Decodes a [data] ByteArray into a CoreImage
 */
suspend fun CoreImage.Companion.encode(image: CoreImage, format: CoreImageFormat, level: Double): ByteArray =
    CoreImageFormatProvider.CURRENT.encode(image, format, level)

/**
 * Encodes a [CoreImage] into a [ByteArray] in the specified [format] (PNG, JPEG, etc.)
 */
suspend fun CoreImage.encodeBytes(format: CoreImageFormat, level: Double = 1.0): ByteArray =
    CoreImageFormatProvider.CURRENT.encode(this, format, level)

/**
 * Whether the current platform supports CoreImage operations.
 */
val CoreImage.Companion.isSupported: Boolean get() = CoreImageFormatProvider.CURRENT.isSupported
