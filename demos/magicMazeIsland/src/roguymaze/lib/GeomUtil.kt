package tfr.korge.jam.roguymaze.lib

import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.BmpSlice
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle
import korlibs.math.geom.Size


fun Bitmap.centered(point: Point): Point = point.copy().sub(this.center())
fun BmpSlice.centered(point: Point): Point = point.copy().sub(this.center())
fun Point.moveUp(marginTop: Number): Point = Point(this.x, this.y - marginTop.toDouble())
fun Point.moveLeft(marginTop: Number): Point = Point(this.x - marginTop.toDouble(), this.y)
fun Point.moveRight(marginTop: Number): Point = Point(this.x + marginTop.toDouble(), this.y)
fun Point.moveDown(marginTop: Number): Point = Point(this.x, this.y + marginTop.toDouble())
fun Bitmap.center(): Point = this.bounds().center()
fun BmpSlice.center(): Point = this.bounds().center()
fun Bitmap.bounds() = Rectangle(0, 0, this.width, this.height)
fun BmpSlice.bounds() = Rectangle(0, 0, this.width, this.height)
fun Rectangle.center() = Point(this.width / 2, this.height / 2)
fun Size.half() = Size(Point(this.p.div(2)))