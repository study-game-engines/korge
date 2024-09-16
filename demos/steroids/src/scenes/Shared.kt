package scenes

import gameplay.Process
import gameplay.getImage
import gameplay.rand
import korlibs.image.bitmap.Bitmap32
import korlibs.image.bitmap.slice
import korlibs.image.color.Colors
import korlibs.korge.view.Container
import korlibs.korge.view.anchor
import korlibs.korge.view.image
import korlibs.korge.view.position
import korlibs.korge.view.scale
import korlibs.math.toIntFloor

/*
fun Container.foto(graph:Int, x:Int, y:Int, size_x:Int, z:Int, flags:Int): Image {
    return image(getImage(graph)){
        position(x,y)
        anchor(0.5, 0.5)
        scale(size_x/100.0, size_x/100.0)
        smoothing = false
    }
}*/

fun Container.foto(graph:Int, x:Int, y:Int, size_x:Int, z:Int, flags:Int) = Foto(this, graph, x, y, size_x, z, flags)
class Foto(parent:Container, val initialGraph:Int, val xx:Int, val yy:Int, val size_x:Int, val z:Int, val flags:Int): Process(parent){
    override suspend fun main() {
        graph = initialGraph
        position(xx,yy)
        anchor(0.5, 0.5)
        scale(size_x/100.0, size_x/100.0)
        smoothing = false
    }
}

fun Container.space(){
    with(Bitmap32(640,480))
    {
        (0..499).forEach {
            this[rand(0,639).toIntFloor(), rand(0,479).toIntFloor()] = Colors.LIGHTGREY
        }
        image(this.slice())
    }
}