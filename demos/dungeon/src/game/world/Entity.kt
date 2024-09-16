package game.world

import game.ai.AI
import korlibs.korge.view.Sprite
import korlibs.korge.view.xy
import math.RangedValue
import math.Vec2
import tileSize

class Entity(
    var pos: Vec2,
    var type: CritterType,
    var ai: AI? = null,
    val sprite: Sprite = Sprite(type.standAnimation).xy(pos.x * tileSize, pos.y * tileSize),
    var life: RangedValue? = if (type != null) RangedValue(type.hp) else null,
    val player: Boolean = false,
    var blocks: Boolean = false,
    val stabber: Boolean = false
) {
    fun isAlive(): Boolean = life?.current ?: 0 > 0

}
