package korlibs.image.core

import com.sun.jna.*
import korlibs.annotations.*
import java.awt.image.*
import java.io.*
import kotlin.time.*

object CoreGraphicsCoreImageFormatProvider : CoreImageFormatProvider {
    // @TODO: this is crashing
    //override suspend fun info(data: ByteArray): CoreImageInfo = autoreleasePool {
    //    val cfdata = CoreFoundation.CFDataCreate(null, data, data.size)
    //    val imgSource = ImageIO.CGImageSourceCreateWithData(data = cfdata, options = null)
    //    val props = ImageIO.CGImageSourceCopyPropertiesAtIndex(imgSource, 0, null)
    //        ?: error("Failed trying to read image in decodeHeaderInternal")
    //    return CoreImageInfo(
    //        width = getIntFromDict(props, ImageIO.kCGImagePropertyPixelWidth),
    //        height = getIntFromDict(props, ImageIO.kCGImagePropertyPixelHeight)
    //    )
    //}

    override suspend fun info(data: ByteArray): CoreImageInfo = AwtCoreImageFormatProvider.info(data)
    override suspend fun decode(data: ByteArray): CoreImage = AwtCoreImage(_decodeInternal(data))
    override suspend fun encode(image: CoreImage, format: CoreImageFormat, level: Double): ByteArray = AwtCoreImageFormatProvider.encode(image, format, level)

    @Keep
    object CoreFoundation {
        // CFNumberGetValue(number: platform.CoreFoundation.CFNumberRef? /* = kotlinx.cinterop.CPointer<cnames.structs.__CFNumber>? */, theType: platform.CoreFoundation.CFNumberType /* = kotlin.Int */, valuePtr: kotlinx.cinterop.CValuesRef<*>?): kotlin.Boolean { /* compiled code */ }
        val LIB = "/System/Library/Frameworks/CoreFoundation.framework/Versions/A/CoreFoundation"
        val lib: NativeLibrary = NativeLibrary.getInstance("/System/Library/Frameworks/CoreFoundation.framework/Versions/A/CoreFoundation")
        val kCFBooleanFalse: Pointer? get() = lib.getGlobalVariableAddress("kCFBooleanFalse")
        val kCFBooleanTrue: Pointer? get() = lib.getGlobalVariableAddress("kCFBooleanTrue")
        //val kCFNumberIntType: Pointer? get() = lib.getGlobalVariableAddress("kCFNumberIntType")
        val kCFNumberIntType: Int get() = 9

        @JvmStatic external fun CFDataCreate(allocator: Pointer?, bytes: Pointer?, length: Int): Pointer?
        @JvmStatic external fun CFDataCreate(allocator: Pointer?, bytes: ByteArray, length: Int): Pointer?
        @JvmStatic external fun CFDataGetBytePtr(data: Pointer?): Pointer?
        @JvmStatic external fun CFDictionaryCreateMutable(allocator: Pointer?, capacity: Int, keyCallbacks: Pointer?, valueCallbacks: Pointer?): Pointer?
        @JvmStatic external fun CFDictionaryAddValue(theDict: Pointer?, key: Pointer?, value: Pointer?): Unit
        @JvmStatic external fun CFDictionaryGetValue(dict: Pointer?, key: Pointer?): Pointer?
        @JvmStatic external fun CFNumberGetValue(number: Pointer?, type: Int, holder: Pointer?)

        init {
            Native.register(LIB)
        }
    }

    @Keep
    object CoreGraphics {
        val LIB = "/System/Library/Frameworks/CoreGraphics.framework/Versions/A/CoreGraphics"

        val kCGImageAlphaNone: Int get() = 0
        val kCGImageAlphaPremultipliedLast: Int get() = 1
        val kCGImageAlphaPremultipliedFirst: Int get() = 2
        val kCGImageAlphaLast: Int get() = 3
        val kCGImageAlphaFirst: Int get() = 4

        @JvmStatic external fun CGMainDisplayID(): Int
        @JvmStatic external fun CGImageGetWidth(ptr: Pointer?): Int
        @JvmStatic external fun CGImageGetHeight(ptr: Pointer?): Int
        @JvmStatic external fun CGImageGetDataProvider(image: Pointer?): Pointer?
        @JvmStatic external fun CGDataProviderCopyData(dataProvider: Pointer?): Pointer?
        @JvmStatic external fun CGColorSpaceCreateDeviceRGB(): Pointer?
        @JvmStatic external fun CGBitmapContextCreate(
            buffer: Pointer?,
            width: Int,
            height: Int,
            bits: Int,
            stride: Int,
            colorSpace: Pointer?,
            alphaInfo: Int
        ): Pointer?
        @JvmStatic external fun CGContextFlush(context: Pointer?)
        @JvmStatic external fun CGContextDrawImage(
            context: Pointer?,
            rect: CGRect.ByValue,
            cgImage: Pointer?
        )
        init {
            Native.register(LIB)
        }
    }
    @Keep
    object ImageIO {
        val LIB = "/System/Library/Frameworks/ImageIO.framework/Versions/A/ImageIO"

        val lib: NativeLibrary = NativeLibrary.getInstance("/System/Library/Frameworks/ImageIO.framework/Versions/A/ImageIO")
        val kCGImageSourceShouldCache: Pointer? get() = lib.getGlobalVariableAddress("kCGImageSourceShouldCache")
        val kCGImageSourceCreateThumbnailWithTransform: Pointer? get() = lib.getGlobalVariableAddress("kCGImageSourceCreateThumbnailWithTransform")
        val kCGImageSourceCreateThumbnailFromImageAlways: Pointer? get() = lib.getGlobalVariableAddress("kCGImageSourceCreateThumbnailFromImageAlways")
        val kCGImagePropertyPixelWidth: Pointer? get() = lib.getGlobalVariableAddress("kCGImagePropertyPixelWidth")
        val kCGImagePropertyPixelHeight: Pointer? get() = lib.getGlobalVariableAddress("kCGImagePropertyPixelHeight")

        @JvmStatic external fun CGImageSourceCreateWithData(data: Pointer?, options: Pointer?): Pointer?
        @JvmStatic external fun CGImageSourceCreateImageAtIndex(imgSource: Pointer?, index: Int, dict: Pointer?): Pointer?
        @JvmStatic external fun CGImageSourceCopyPropertiesAtIndex(imgSource: Pointer?, index: Int, dict: Pointer?): Pointer?

        init {
            Native.register(LIB)
        }
    }

    open class CGRect : Structure {
        @JvmField var x: Double = 0.0
        @JvmField var y: Double = 0.0
        @JvmField var width: Double = 0.0
        @JvmField var height: Double = 0.0

        constructor() : super()
        constructor(peer: Pointer?) : super(peer)

        override fun getFieldOrder() = listOf("x", "y", "width", "height")

        class ByReference : CGRect(), Structure.ByReference
        class ByValue : CGRect(), Structure.ByValue

        companion object {
            fun make(x: Double, y: Double, width: Double, height: Double): ByValue {
                val it = ByValue()
                it.x = 0.0
                it.y = 0.0
                it.width = width.toDouble()
                it.height = height.toDouble()
                it.write()
                return it
            }
        }
    }
    @Keep
    object ObjC {
        @JvmStatic external fun objc_autoreleasePoolPush(): Pointer?
        @JvmStatic external fun objc_autoreleasePoolPop(ptr: Pointer?)
        init {
            Native.register("/usr/lib/libobjc.A.dylib")
        }
    }

    private fun _decodeInternal(data: ByteArray): BufferedImage = autoreleasePool {
        val dataCreate = CoreFoundation.CFDataCreate(null, data, data.size)
        val imgSource = ImageIO.CGImageSourceCreateWithData(dataCreate, null)
        val dict = CoreFoundation.CFDictionaryCreateMutable(null, 0, null, null)
        CoreFoundation.CFDictionaryAddValue(dict, ImageIO.kCGImageSourceShouldCache!!.getPointer(0L), CoreFoundation.kCFBooleanFalse!!.getPointer(0L))
        CoreFoundation.CFDictionaryAddValue(dict, ImageIO.kCGImageSourceCreateThumbnailWithTransform!!.getPointer(0L), CoreFoundation.kCFBooleanFalse!!.getPointer(0L))
        CoreFoundation.CFDictionaryAddValue(dict, ImageIO.kCGImageSourceCreateThumbnailFromImageAlways!!.getPointer(0L), CoreFoundation.kCFBooleanTrue!!.getPointer(0L))
        //println("PREPARE:" + p.getElapsedAndRestart())
        val cgImage = ImageIO.CGImageSourceCreateImageAtIndex(imgSource, 0, dict)
        //println("SOURCE:" + p.getElapsedAndRestart())

        val width = CoreGraphics.CGImageGetWidth(cgImage)
        val height = CoreGraphics.CGImageGetHeight(cgImage)

        //println("CoreGraphics.kCGImageAlphaPremultipliedLast=${CoreGraphics.kCGImageAlphaPremultipliedLast}")
        //val realPremultiplied = props.premultipliedSure
        val realPremultiplied = true
        val alphaInfo = when (realPremultiplied) {
            true -> CoreGraphics.kCGImageAlphaPremultipliedLast
            false -> CoreGraphics.kCGImageAlphaLast
            //true -> CoreGraphics.kCGImageAlphaPremultipliedFirst
            //false -> CoreGraphics.kCGImageAlphaFirst
        }
        val colorSpace = CoreGraphics.CGColorSpaceCreateDeviceRGB()

        if (width * height <= 0) {
            error("Invalid size size=${width}x${height}")
        }
        val pixels = Memory((width * height * 4).toLong()).also { it.clear() }

        val context = CoreGraphics.CGBitmapContextCreate(
            pixels, width, height, 8,
            (width * 4), colorSpace, alphaInfo
        )

        //val data = CoreGraphics.CGDataProviderCopyData(CoreGraphics.CGImageGetDataProvider(image))
        //println("GET_DATA:" + p.getElapsedAndRestart())
        //val pixels = CoreFoundation.CFDataGetBytePtr(data)


        val rect = CGRect.make(0.0, 0.0, width.toDouble(), height.toDouble())
        CoreGraphics.CGContextDrawImage(context, rect, cgImage)
        CoreGraphics.CGContextFlush(context)

        //println("GET_DATA:" + p.getElapsedAndRestart())


        BufferedImage(width, height, if (realPremultiplied) BufferedImage.TYPE_INT_ARGB_PRE else BufferedImage.TYPE_INT_ARGB).also { bufferedImage ->
            val bufferedImageData = (bufferedImage.raster.dataBuffer as DataBufferInt).data
            pixels.read(0L, bufferedImageData, 0, width * height)
            BGRAtoRGBA(bufferedImageData)
        }
    }

    private inline fun <T> autoreleasePool(body: () -> T): T {
        val ptr = ObjC.objc_autoreleasePoolPush()
        try {
            return body()
        } finally {
            ObjC.objc_autoreleasePoolPop(ptr)
        }
    }

    private fun getIntFromDict(props: Pointer?, key: Pointer?): Int {
        val mem = Memory(8)
        CoreFoundation.CFNumberGetValue(CoreFoundation.CFDictionaryGetValue(props, key), CoreFoundation.kCFNumberIntType, mem)
        return mem.getInt(0L)
    }
}