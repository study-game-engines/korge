package j4k.candycrush.lib

import korlibs.image.bitmap.Bitmap
import korlibs.math.geom.Point
import korlibs.math.geom.Rectangle

fun Bitmap.centered(point: Point): Point = point.sub(this.center())
fun Point.top(marginTop: Number): Point = Point(this.x, this.y + marginTop.toDouble())
fun Bitmap.center(): Point = this.bounds().center()
fun Bitmap.bounds() = Rectangle(0, 0, this.width, this.height)
fun Rectangle.center() = Point(this.width / 2, this.height / 2)