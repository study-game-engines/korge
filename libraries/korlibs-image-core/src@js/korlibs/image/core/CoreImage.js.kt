package korlibs.image.core

import kotlinx.browser.*
import kotlinx.coroutines.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.url.*
import org.w3c.files.*
import kotlin.coroutines.*
import kotlin.io.encoding.*

private fun hasDocument(): Boolean = js("(typeof document !== 'undefined')").unsafeCast<Boolean>()

actual val CoreImageFormatProvider_default: CoreImageFormatProvider by lazy {
    when {
        hasDocument() -> HtmlCoreImageFormatProvider
        else -> StbiCoreImageFormatProvider
    }
}

@OptIn(ExperimentalEncodingApi::class)
object HtmlCoreImageFormatProvider : CoreImageFormatProvider {
    override suspend fun info(data: ByteArray): CoreImageInfo = generateTemporalURLForBytes(data) {
        val img = loadImage(it)
        CoreImageInfo(img.naturalWidth, img.naturalHeight)
    }

    override suspend fun decode(data: ByteArray): CoreImage = HtmlCanvasCoreImage(decodeToCanvas(data))
    suspend fun decodeURL(url: String): CoreImage = HtmlCanvasCoreImage(loadCanvas(url))

    override suspend fun encode(image: CoreImage, format: CoreImageFormat, level: Double): ByteArray {
        val str = image.toCanvas().canvas.unsafeCast<HTMLCanvasElement>().toDataURL("image/${format.name.lowercase()}", level)
        return Base64.decode(str.substringAfter(','))
    }

    inline fun <T> generateTemporalURLForBytes(bytes: ByteArray, type: String = "image/png", block: (url: String) -> T): T {
        val blob = Blob(arrayOf(bytes), BlobPropertyBag(type = type))
        val blobURL = URL.createObjectURL(blob)
        try {
            return block(blobURL)
        } finally {
            URL.revokeObjectURL(blobURL)
        }
    }

    suspend fun decodeToCanvas(bytes: ByteArray): HTMLCanvasElement = generateTemporalURLForBytes(bytes) { loadCanvas(it) }

    fun imageToCanvas(img: HTMLImageElement): HTMLCanvasElement = imageToCanvas(img, img.width, img.height)

    fun imageToCanvas(img: HTMLImageElement, width: Int, height: Int): HTMLCanvasElement {
        val canvas = createCanvas(width, height)
        val ctx: CanvasRenderingContext2D = canvas.getContext("2d").unsafeCast<CanvasRenderingContext2D>()
        ctx.drawImage(img.unsafeCast<CanvasImageSource>(), 0.0, 0.0)
        return canvas
    }

    suspend fun loadImage(url: String): HTMLImageElement = suspendCancellableCoroutine { c ->
        val img = document.createElement("img").unsafeCast<HTMLImageElement>()
        img.onload = { c.resume(img.unsafeCast<HTMLImageElement>()) }
        img.onerror = { _, _, _, _, _ -> c.resumeWithException(RuntimeException("error loading image $url")) }
        img.src = url
    }

    suspend fun loadCanvas(url: String): HTMLCanvasElement {
        return imageToCanvas(loadImage(url))
    }

    fun createCanvas(width: Int, height: Int): HTMLCanvasElement =
        document.createElement("canvas").unsafeCast<HTMLCanvasElement>().also { it.width = width; it.height = height }
}

private val isLittleEndian: Boolean = Uint8Array(Int32Array(arrayOf(1)).buffer)[0].toInt() == 1
private fun bswap32(v: Int): Int = (v ushr 24) or (v shl 24) or ((v and 0xFF00) shl 8) or (v ushr 8) and 0xFF00
private fun bswap32(v: IntArray, start: Int = 0, end: Int = v.size) { for (n in start until end) v[n] = bswap32(v[n]) }

fun CoreImage.toCanvas(): HtmlCanvasCoreImage {
    val bmp = this.to32().depremultiplied()
    val canvas = HtmlCoreImageFormatProvider.createCanvas(bmp.width, bmp.height)
    val ctx: CanvasRenderingContext2D = canvas.getContext2d()
    val imageData = ctx.createImageData(bmp.width.toDouble(), bmp.height.toDouble())

    // @TODO: Conversions? RGBA, BGRA, etc.?
    val out = Int32Array(imageData.data.buffer).unsafeCast<IntArray>()
    bmp.data.copyInto(out)
    if (!isLittleEndian) bswap32(out)

    ctx.putImageData(imageData, 0.0, 0.0)
    return HtmlCanvasCoreImage(canvas)
}

fun HTMLCanvasElement.getContext2d(): CanvasRenderingContext2D = getContext("2d").unsafeCast<CanvasRenderingContext2D>()

class HtmlCanvasCoreImage(val canvas: HTMLCanvasElement) : CoreImage {
    override val width: Int get() = canvas.width
    override val height: Int get() = canvas.height
    override val native: Any get() = canvas
    override val bpp: Int get() = 32
    override val premultiplied: Boolean get() = true
    override fun to32(): CoreImage32 {
        val ctx = canvas.getContext2d()
        val data = ctx.getImageData(0.0, 0.0, width.toDouble(), height.toDouble())
        // @TODO: Conversions? RGBA, BGRA, etc.?
        val out = Int32Array(data.data.buffer).unsafeCast<IntArray>()
        if (!isLittleEndian) bswap32(out)
        return CoreImage32(width, height, out, false).premultiplied()
    }
}

