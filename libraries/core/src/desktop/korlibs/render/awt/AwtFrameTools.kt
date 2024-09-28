package korlibs.render.awt

import korlibs.datastructure.extraPropertyThis
import korlibs.event.DropFileEvent
import korlibs.event.EventListener
import korlibs.event.GestureEvent
import korlibs.event.reset
import korlibs.graphics.AG
import korlibs.image.awt.toAwt
import korlibs.io.dynamic.Dyn
import korlibs.io.dynamic.dyn
import korlibs.io.file.VfsFile
import korlibs.io.file.std.toVfs
import korlibs.math.geom.Size
import korlibs.math.toIntCeil
import korlibs.platform.Platform
import korlibs.render.CustomCursor
import korlibs.render.GameWindow
import korlibs.render.ICursor
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame

fun JFrame.setIconIncludingTaskbarFromResource(path: String) {
    runCatching {
        val awtImageURL = AwtGameWindow::class.java.getResource("/$path")
            ?: AwtGameWindow::class.java.getResource(path)
            ?: ClassLoader.getSystemResource(path)
        setIconIncludingTaskbarFromImage(awtImageURL?.let { ImageIO.read(it) })
    }
}

fun JFrame.setIconIncludingTaskbarFromImage(awtImage: BufferedImage?) {
    val frame = this
    runCatching {
        frame.iconImage = awtImage?.getScaledInstance(32, 32, Image.SCALE_SMOOTH)
        Dyn.global["java.awt.Taskbar"].dynamicInvoke("getTaskbar").dynamicInvoke("setIconImage", awtImage)
    }
}

var JFrame._isAlwaysOnTop: Boolean
    get() = isAlwaysOnTop
    set(value) {
        isAlwaysOnTop = value
    }

var Component.visible: Boolean
    get() = isVisible
    set(value) {
        isVisible = value
    }

var JFrame.isFullScreen: Boolean
    get() {
        val frame = this
        return when {
            Platform.isMac -> frame.rootPane.bounds == frame.bounds
            else -> GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow == frame
        }
    }
    set(value) {
        val frame = this
        //println("fullscreen: $fullscreen -> $value")
        if (isFullScreen != value) {
            when {
                Platform.isMac -> {
                    //println("TOGGLING!")
                    if (isFullScreen != value) {
                        EventQueue.invokeLater {
                            try {
                                //println("INVOKE!: ${getClass("com.apple.eawt.Application").invoke("getApplication")}")
                                Dyn.global["com.apple.eawt.Application"]
                                    .dynamicInvoke("getApplication")
                                    .dynamicInvoke("requestToggleFullScreen", frame)
                            } catch (e: Throwable) {
                                if (e::class.qualifiedName != "java.lang.reflect.InaccessibleObjectException") {
                                    e.printStackTrace()
                                }
                                GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = if (value) frame else null
                                frame.isVisible = true
                            }
                        }
                    }
                }
                else -> {
                    GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow = if (value) frame else null

                    //frame.extendedState = if (value) JFrame.MAXIMIZED_BOTH else JFrame.NORMAL
                    //frame.isUndecorated = value
                    frame.isVisible = true
                    //frame.isAlwaysOnTop = true
                }
            }
        }
    }

fun JFrame.initTools() {
    if (Platform.isMac) {
        try {
            Dyn.global["com.apple.eawt.FullScreenUtilities"].dynamicInvoke("setWindowCanFullScreen", this, true)
            //Dyn.global["com.apple.eawt.FullScreenUtilities"].invoke("addFullScreenListenerTo", frame, listener)
        } catch (e: Throwable) {
            if (e::class.qualifiedName != "java.lang.reflect.InaccessibleObjectException") {
                e.printStackTrace()
            }
        }
    }
}

fun Component.setKorgeDropTarget(dispatcher: EventListener) {
    val dropFileEvent = DropFileEvent()
    fun dispatchDropfileEvent(type: DropFileEvent.Type, files: List<VfsFile>?) = dispatcher.dispatch(dropFileEvent.reset {
        this.type = type
        this.files = files
    })
    dropTarget = object : DropTarget() {
        init {
            this.addDropTargetListener(object : DropTargetAdapter() {
                override fun drop(dtde: DropTargetDropEvent) {
                    //println("drop")
                    dtde.acceptDrop(DnDConstants.ACTION_COPY)
                    dispatchDropfileEvent(DropFileEvent.Type.DROP, (dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>).map { it.toVfs() })
                    dispatchDropfileEvent(DropFileEvent.Type.END, null)
                }
            })
        }

        override fun dragEnter(dtde: DropTargetDragEvent) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY)
            //dispatchDropfileEvent(DropFileEvent.Type.ENTER, null)
            dispatchDropfileEvent(DropFileEvent.Type.START, null)
            //println("dragEnter")
            super.dragEnter(dtde)
        }

        override fun dragOver(dtde: DropTargetDragEvent) {
            dtde.acceptDrag(DnDConstants.ACTION_COPY)
            super.dragOver(dtde)
        }

        override fun dragExit(dte: DropTargetEvent) {
            //dispatchDropfileEvent(DropFileEvent.Type.EXIT, null)
            dispatchDropfileEvent(DropFileEvent.Type.END, null)
            super.dragExit(dte)
        }
    }
}

val Component.devicePixelRatio: Double
    get() {
        if (GraphicsEnvironment.isHeadless()) {
            return 1.0
        } else {
            // transform
            // https://stackoverflow.com/questions/20767708/how-do-you-detect-a-retina-display-in-java
            val config = graphicsConfiguration
                ?: GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration
            return config.defaultTransform.scaleX
        }
    }

val Component.pixelsPerInch: Double
    get() {
        if (GraphicsEnvironment.isHeadless()) {
            return AG.defaultPixelsPerInch
        } else {
            // maybe this is not just windows specific :
            // https://stackoverflow.com/questions/32586883/windows-scaling
            // somehow this value is not update when you change the scaling in the windows settings while the jvm is running :(
            return Toolkit.getDefaultToolkit().screenResolution.toDouble()
        }
    }


val korlibs.render.Cursor.jvmCursor: java.awt.Cursor
    get() = java.awt.Cursor(
        when (this) {
            korlibs.render.Cursor.DEFAULT -> java.awt.Cursor.DEFAULT_CURSOR
            korlibs.render.Cursor.CROSSHAIR -> java.awt.Cursor.CROSSHAIR_CURSOR
            korlibs.render.Cursor.TEXT -> java.awt.Cursor.TEXT_CURSOR
            korlibs.render.Cursor.HAND -> java.awt.Cursor.HAND_CURSOR
            korlibs.render.Cursor.MOVE -> java.awt.Cursor.MOVE_CURSOR
            korlibs.render.Cursor.WAIT -> java.awt.Cursor.WAIT_CURSOR
            korlibs.render.Cursor.RESIZE_EAST -> java.awt.Cursor.E_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_SOUTH -> java.awt.Cursor.S_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_WEST -> java.awt.Cursor.W_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_NORTH -> java.awt.Cursor.N_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_NORTH_EAST -> java.awt.Cursor.NE_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_NORTH_WEST -> java.awt.Cursor.NW_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_SOUTH_EAST -> java.awt.Cursor.SE_RESIZE_CURSOR
            korlibs.render.Cursor.RESIZE_SOUTH_WEST -> java.awt.Cursor.SW_RESIZE_CURSOR
            else -> java.awt.Cursor.DEFAULT_CURSOR
        }
    )

val CustomCursor.jvmCursor: java.awt.Cursor by extraPropertyThis {
    val toolkit = Toolkit.getDefaultToolkit()
    val size = toolkit.getBestCursorSize(bounds.width.toIntCeil(), bounds.height.toIntCeil())
    val result = this.createBitmap(Size(size.width, size.height))
    //println("BITMAP SIZE=${result.bitmap.size}, hotspot=${result.hotspot}")
    val hotspotX = result.hotspot.x.coerceIn(0, result.bitmap.width - 1)
    val hotspotY = result.hotspot.y.coerceIn(0, result.bitmap.height - 1)
    toolkit.createCustomCursor(result.bitmap.toAwt(), Point(hotspotX, hotspotY), name).also {
        //println("CUSTOM CURSOR: $it")
    }
}

val ICursor.jvmCursor: java.awt.Cursor
    get() {
        return when (this) {
            is korlibs.render.Cursor -> this.jvmCursor
            is CustomCursor -> this.jvmCursor
            else -> java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR)
        }
    }

fun Component.hapticFeedbackGenerate(kind: GameWindow.HapticFeedbackKind) {
    when {
        else -> {
            Unit
        }
    }
}

//fun Component.registerGestureListeners(dispatcher: EventListener) {
fun Component.registerGestureListeners(dispatcher: GameWindow) {
    if (!Platform.isMac) return

    val contentComponent = this
    val gestureEvent = GestureEvent()
    try {
        GameWindow.logger.info { "MacOS registering gesture listener..." }

        val gestureListener = java.lang.reflect.Proxy.newProxyInstance(
            ClassLoader.getSystemClassLoader(),
            arrayOf(
                Class.forName("com.apple.eawt.event.GestureListener"),
                Class.forName("com.apple.eawt.event.MagnificationListener"),
                Class.forName("com.apple.eawt.event.RotationListener"),
                Class.forName("com.apple.eawt.event.SwipeListener"),
            )
        ) { proxy, method, args ->
            try {
                when (method.name) {
                    "magnify" -> {
                        val magnification = args[0].dyn.dynamicInvoke("getMagnification")
                        //println("magnify: $magnification")
                        dispatcher.queue {
                            dispatcher.dispatch(gestureEvent.also {
                                it.type = GestureEvent.Type.MAGNIFY
                                it.id = 0
                                it.amount = magnification.float
                            })
                        }
                    }

                    "rotate" -> {
                        val rotation = args[0].dyn.dynamicInvoke("getRotation")
                        //println("rotate: $rotation")
                        dispatcher.queue {
                            dispatcher.dispatch(gestureEvent.also {
                                it.type = GestureEvent.Type.ROTATE
                                it.id = 0
                                it.amount = rotation.float
                            })
                        }
                    }

                    "swipedUp", "swipedDown", "swipedLeft", "swipedRight" -> {
                        dispatcher.queue {
                            dispatcher.dispatch(gestureEvent.also {
                                it.type = GestureEvent.Type.SWIPE
                                it.id = 0
                                it.amountX = 0f
                                it.amountY = 0f
                                when (method.name) {
                                    "swipedUp" -> it.amountY = -1f
                                    "swipedDown" -> it.amountY = +1f
                                    "swipedLeft" -> it.amountX = -1f
                                    "swipedRight" -> it.amountX = +1f
                                }
                            })
                        }
                    }

                    else -> {
                        //println("gestureListener: $method, ${args.toList()}")
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            args[0].dyn.dynamicInvoke("consume")
        }

        val clazz = Dyn.global["com.apple.eawt.event.GestureUtilities"]
        GameWindow.logger.info { " -- GestureUtilities=$clazz" }
        clazz.dynamicInvoke("addGestureListenerTo", contentComponent, gestureListener)

        //val value = (contentComponent as JComponent).getClientProperty("com.apple.eawt.event.internalGestureHandler");
        //println("value $value");
        //GestureUtilities.addGestureListenerTo(p, ga);
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}
