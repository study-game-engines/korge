package tfr.korge.jam.roguymaze.input

import korlibs.event.MouseButton
import korlibs.event.MouseEvent
import korlibs.event.TouchEvent
import korlibs.korge.view.View
import korlibs.korge.view.Views
import korlibs.math.geom.Point

class DragListener(
    override val view: View,
    private val maximumDragDistance: Int,
    private val dragEventListener: DragEventListener
) : TouchComponent, MouseComponent {

    private var start = Point.ZERO
    private var end = Point.ZERO

    private fun dragDistance() = start.distanceTo(end)

    data class DragEvent(val start: Point, val end: Point)

    private fun reset() {
        start = Point.ZERO
        end = Point.ZERO
    }

    interface DragEventListener {
        fun onDragEvent(dragEvent: DragEvent)
    }

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        if (event.button == MouseButton.LEFT) {
            when (event.type) {
                MouseEvent.Type.DOWN -> {
                    start = event.point()
                }
                MouseEvent.Type.DRAG, MouseEvent.Type.MOVE -> {
                    if (startedDrag()) {
                        end = event.point()
                        if (dragDistance() > maximumDragDistance) {
                            notifyDragListener()
                            reset()
                        }
                    }
                }
                MouseEvent.Type.UP -> {
                    end = event.point()
                    if (startedDrag()) {
                        notifyDragListener()
                        reset()
                    }
                }
            }
        }

    }

    fun MouseEvent.point() = project(Point(this.x, y))

    fun project(point: Point): Point {
        return view.globalToLocal(point.x, point.y, point)
    }

    private fun startedDrag() = start != Point.ZERO

    private fun notifyDragListener() {
        dragEventListener.onDragEvent(DragEvent(start, end))
    }

    override fun onTouchEvent(views: Views, e: TouchEvent) {
        when (e.type) {
            TouchEvent.Type.START -> print("start")
            TouchEvent.Type.MOVE -> print("start")
            TouchEvent.Type.END -> {
                print("end")
                val touch = e.touches.first()
                println(touch.start.distanceTo(touch.current))
            }
        }

    }

}