package j4k.candycrush.input

import j4k.candycrush.lib.*
import j4k.candycrush.model.*
import j4k.candycrush.renderer.*
import korlibs.event.MouseEvent
import korlibs.image.bitmap.Bitmap
import korlibs.korge.view.View
import korlibs.korge.view.Views
import korlibs.math.geom.Point
import korlibs.math.geom.Vector2D

class DragListener(
    override val view: View,
    private val maximumDragDistance: Int,
    private val dragEventListener: DragEventListener
) : MouseComponent {

    private var start : Point = Point()
    private var end : Point = Point()

    private fun dragDistance() = start.distanceTo(end)

    data class DragEvent(val start: Point, val end: Point)

    private fun reset() {
        start.setToZero()
        end.setToZero()
    }

    interface DragEventListener {
        fun onDragEvent(dragEvent: DragEvent)
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                start.setToMouseXY(views)
            }
            MouseEvent.Type.DRAG, MouseEvent.Type.MOVE -> {
                if (startedDrag()) {
                    end.setToMouseXY(views)
                    if (dragDistance() > maximumDragDistance) {
                        notifyDragListener()
                        reset()
                    }
                }
            }
            MouseEvent.Type.UP -> {
                end.setToMouseXY(views)
                if (startedDrag()) {
                    notifyDragListener()
                    reset()
                }
            }
            else -> {
                // Ignore all other mouse events
            }
        }
    }

    private fun Point.setToMouseXY(views: Views): Point {
        return view.localMousePos(views,this)
    }

    private fun startedDrag() = start != Point.ZERO

    private fun notifyDragListener() {
        dragEventListener.onDragEvent(DragEvent(start, end))
    }

}
