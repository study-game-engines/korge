package korlibs.korge.view

import korlibs.graphics.AG
import korlibs.inject.Injector
import korlibs.io.resources.ResourcesContainer
import korlibs.korge.input.Input
import korlibs.korge.input.InputKeys
import korlibs.korge.view.property.ViewProperty
import korlibs.math.annotations.RootViewDslMarker
import korlibs.math.geom.Point
import korlibs.math.geom.Size
import korlibs.render.GameWindow
import kotlinx.coroutines.CoroutineScope

/**
 * Singleton root [View] and [Container] that contains a reference to the [Views] singleton and doesn't have any parent.
 */
@RootViewDslMarker
open class Stage internal constructor(override val views: Views) : FixedSizeContainer(), View.Reference, CoroutineScope by views, ViewsContainer, ResourcesContainer, BoundsProvider by views.bp, InvalidateNotifier {

    override var clip: Boolean by views::clipBorders
    override var unscaledSize: Size by views::virtualSizeDouble

    val keys: InputKeys get() = views.input.keys
    val input: Input get() = views.input
    val injector: Injector get() = views.injector
    val ag: AG get() = views.ag
    val gameWindow: GameWindow get() = views.gameWindow
    override val resources get() = views.resources
    override val stage: Stage get() = this
    override val _invalidateNotifierForChildren: InvalidateNotifier get() = this

    init {
        this._stage = this
        this._invalidateNotifier = this
    }

    /** Mouse coordinates relative to the [Stage] singleton */
    val mousePos: Point get() = localMousePos(views)

    //override fun getLocalBoundsInternal(out: Rectangle) {
    //    out.setTo(0.0, 0.0, views.virtualWidth, views.virtualHeight)
    //}
    ////override fun hitTest(x: Double, y: Double): View? = super.hitTest(x, y) ?: this
    //override fun renderInternal(ctx: RenderContext) {
    //    if (views.clipBorders) {
    //        ctx.useCtx2d { ctx2d ->
    //            ctx.rectPool.alloc { _tempWindowBounds ->
    //                ctx2d.scissor(views.globalToWindowBounds(this.globalBounds, _tempWindowBounds)) {
    //                    super.renderInternal(ctx)
    //                }
    //            }
    //        }
    //    } else {
    //        super.renderInternal(ctx)
    //    }
    //}

    @Suppress("unused")
    @ViewProperty(min = 0.0, max = 2000.0, groupName = "Stage")
    private var virtualSize: Point
        get() = Point(views.virtualWidthDouble, views.virtualHeightDouble)
        set(value) {
            views.virtualWidthDouble = value.x
            views.virtualHeightDouble = value.y
            views.gameWindow.queue {
                views.resized()
            }
        }

    override fun invalidatedView(view: BaseView?) {
        views.invalidatedView(view)
    }

    override fun toString(): String = "Stage"

}
