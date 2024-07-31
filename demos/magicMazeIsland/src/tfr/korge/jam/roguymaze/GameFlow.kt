package tfr.korge.jam.roguymaze

import com.soywiz.klogger.Logger
import com.soywiz.korinject.AsyncInjector
import tfr.korge.jam.roguymaze.InputEvent.Action
import tfr.korge.jam.roguymaze.audio.SoundMachine
import tfr.korge.jam.roguymaze.level.WorldFactory
import tfr.korge.jam.roguymaze.lib.EventBus
import tfr.korge.jam.roguymaze.math.PositionGrid.Position
import tfr.korge.jam.roguymaze.model.Room
import tfr.korge.jam.roguymaze.model.Team.Hero
import tfr.korge.jam.roguymaze.model.Tile
import tfr.korge.jam.roguymaze.model.World
import tfr.korge.jam.roguymaze.renderer.WorldComponent

/**
 * Global game cycle
 */
class GameFlow(
    private val world: World,
    private val worldFactory: WorldFactory,
    private val worldComponent: WorldComponent,
    private val bus: EventBus,
    private val mechanics: GameMechanics,
    private val soundMachine: SoundMachine
) {

    init {
        bus.register<InputEvent> { handleInput(it) }
        bus.register<TileClickedEvent> { clickedTile(it) }
        bus.register<ChangePlayerEvent> { handleChangePlayerId(it) }
        bus.register<ChangeRoomEvent> {
            log.info { "Change room to ${it.roomName}" }
            world.roomName = it.roomName
        }
    }

    private fun clickedTile(tileEvent: TileClickedEvent) {
        val hero = world.getSelectedHero()
        if (tileEvent.tile == Tile.Grass) {
            if (tileEvent.gridPos.distance(hero.pos()).toInt() == 1) {
                val tilePos = tileEvent.gridPos
                val heroPos = hero.pos()

                if (tilePos.x > heroPos.x && tilePos.y == heroPos.y && Action.HeroRight.isAllowed()) {
                    moveHero(hero.number, Direction.Right)
                } else if (tilePos.x < heroPos.x && tilePos.y == heroPos.y && Action.HeroLeft.isAllowed()) {
                    moveHero(hero.number, Direction.Left)
                } else if (tilePos.x == heroPos.x && tilePos.y > heroPos.y && Action.HeroDown.isAllowed()) {
                    moveHero(hero.number, Direction.Down)
                } else if (tilePos.x == heroPos.x && tilePos.y < heroPos.y && Action.HeroUp.isAllowed()) {
                    moveHero(hero.number, Direction.Up)
                }
            }
        }
    }

    fun Action.isAllowed() = world.getAllowedActions().contains(this)

    private fun handleChangePlayerId(event: ChangePlayerEvent) {
        log.info { "Selected Player: ${event.playerId}" }
        world.selectedPlayer = event.playerId
    }

    private fun handleInput(inputEvent: InputEvent) {
        val playerId = inputEvent.heroNumber
        if (playerId != 0) {
            executeInput(inputEvent, playerId)
        }
    }

    private fun executeInput(inputEvent: InputEvent, playerId: Int) {
        when (inputEvent.action) {
            Action.MapMoveUp -> mechanics.moveMapUp()
            Action.MapMoveDown -> mechanics.moveMapDown()
            Action.MapMoveLeft -> mechanics.moveMapLeft()
            Action.MapMoveRight -> mechanics.moveMapRight()
            Action.MapZoomIn -> mechanics.zoomIn()
            Action.MapZoomOut -> mechanics.zoomOut()
            Action.SelectHero -> selectHero(playerId)
            Action.HeroLeft -> moveHero(playerId, Direction.Left)
            Action.HeroRight -> moveHero(playerId, Direction.Right)
            Action.HeroUp -> moveHero(playerId, Direction.Up)
            Action.HeroDown -> moveHero(playerId, Direction.Down)
            Action.ActionSearch -> findNewRoom()
            Action.FoundNextRoom -> {
                if (inputEvent.roomId != null && playerId != 0) {
                    findNewRoom(playerId, inputEvent.roomId)
                }
            }
            Action.Unknown -> {
                log.debug { "What should we do with a drunken sailor?" }
            }
        }
    }

    fun moveHero(direction: Direction) {
        moveHero(world.selectedHero, direction)
    }

    private fun selectHero(selectedHero: Int) {
        world.selectedHero = selectedHero
    }

    companion object {
        val log = Logger("GameFlow")

        suspend operator fun invoke(injector: AsyncInjector): GameFlow {
            injector.mapSingleton {
                GameFlow(get(), get(), get(), get(), get(), get())
            }
            return injector.get()
        }
    }

    enum class Direction {
        Up, Down, Left, Right;

        fun opposite() = when (this) {
            Up -> Down
            Down -> Up
            Left -> Right
            Right -> Left
        }
    }

    fun Position.move(direction: Direction) = when (direction) {
        Direction.Left -> Position(x - 1, y)
        Direction.Right -> Position(x + 1, y)
        Direction.Up -> Position(x, y - 1)
        Direction.Down -> Position(x, y + 1)
    }


    fun moveHero(playerNumber: Int, direction: Direction) {
        val heroModel: Hero = world.getHero(playerNumber)
        val playerComponent = worldComponent.getHero(heroModel)
        val playerPos = heroModel.pos()
        val nextAbsolutePos = playerPos.move(direction)
        val nextField = world.getGroundTileCellAbsolute(nextAbsolutePos)
        val currentRoom = world.getRoom(playerPos)
        val nextRoom = world.getRoom(nextAbsolutePos)
        val blockedByWall = isBlockedByWall(direction, currentRoom, playerPos, nextRoom, nextAbsolutePos)

        val takenByPlayer = world.team.isTaken(nextAbsolutePos)
        if (currentRoom != null && nextField.tile == Tile.Grass && !takenByPlayer && !blockedByWall && !heroModel.inHome) {
            heroModel.setPos(nextAbsolutePos)
            playerComponent.move(direction)
            val currentItem = currentRoom.getItemTileCellRelative(nextField.position)
            if (currentItem.tile.isMask() && playerNumber == currentItem.tile.getPlayerNumber()) {
                heroModel.collectedMask = true
                log.info { "Player $playerNumber found mask ${currentItem.tile}!" }
                currentRoom.removeItem(nextField.position)
                bus.send(FoundMaskEvent(playerNumber))
            }
            if (currentItem.tile.isHome() && playerNumber == currentItem.tile.getPlayerNumber() && heroModel.collectedMask) {
                heroModel.inHome = true
                log.info { "Player $playerNumber found a sweet home on ${currentItem.tile}!" }
                currentRoom.setFinish(nextField.position, heroModel)
                bus.send(FoundHomeEvent(playerNumber))
            }
            soundMachine.playDropGround()
        } else {
            val taken = if (takenByPlayer) "taken by player" else ""
            log.info { "Illegal Move: $nextAbsolutePos -> $nextField (taken: $taken, border: $blockedByWall" }
            playerComponent.moveIllegal(direction)
        }
    }

    private fun isBlockedByWall(
        direction: Direction, currentRoom: Room?, playerPos: Position, nextRoom: Room?, nextAbsolutePos: Position
    ): Boolean {
        return when (direction) {
            Direction.Left -> currentRoom?.getBorderLeftAbsolute(
                playerPos
            ) == Tile.Border || nextRoom?.getBorderRightAbsolute(nextAbsolutePos) == Tile.Border
            Direction.Right -> currentRoom?.getBorderRightAbsolute(
                playerPos
            ) == Tile.Border || nextRoom?.getBorderLeftAbsolute(nextAbsolutePos) == Tile.Border
            Direction.Up -> currentRoom?.getBorderTopAbsolute(
                playerPos
            ) == Tile.Border || nextRoom?.getBorderBottomAbsolute(nextAbsolutePos) == Tile.Border
            Direction.Down -> currentRoom?.getBorderBottomAbsolute(
                playerPos
            ) == Tile.Border || nextRoom?.getBorderTopAbsolute(nextAbsolutePos) == Tile.Border
        }
    }


    private fun Room.removeItem(relativePosition: Position) {
        worldComponent.getRoom(this.id)?.items?.removeImage(relativePosition)
        this.items[relativePosition] = Tile.Empty
    }

    private fun Room.setItem(relativePosition: Position, tile: Tile) {
        worldComponent.getRoom(this.id)?.items?.addTile(relativePosition, tile)
        this.items[relativePosition] = tile
    }

    private fun Room.setFinish(relativePosition: Position, hero: Hero) {
        removeItem(relativePosition)
        setItem(relativePosition, Tile.getFinish(hero.number))
    }

    fun reset() {
    }

    fun findNewRoom(playerNumber: Int = world.selectedHero, nextRoomId: Int? = null) {
        val heroModel: Hero = world.getHero(playerNumber)
        val playerPos = heroModel.pos()
        val playerItem = world.getItemTileCellAbsolute(playerPos)
        val playerRelativePos = playerItem.position

        if (playerItem.tile.isDoorForHero(playerNumber)) {
            val currentRoom: Room = world.getRoom(playerPos) ?: throw IllegalStateException("Hero outside room")
            currentRoom.removeItem(playerRelativePos)

            if (playerItem.tile.isExit()) {
                openDoorToNextRoom(currentRoom, playerPos, nextRoomId)?.let { nextRoom ->
                    bus.send(InputEvent(Action.FoundNextRoom, heroNumber = playerNumber, roomId = nextRoom.id))
                }
            } else {
                log.info { "No Exit on the current tile: $playerPos" }
            }
        } else {
            log.info { "Tried to unlock door with wrong player: $playerNumber -> ${playerItem.tile}" }
        }
    }

    private fun openDoorToNextRoom(currentRoom: Room, playerPos: Position, nextRoomId: Int?): Room? {
        val exit = currentRoom.getExit(playerPos)
        val direction = exit.direction()!!

        val nextAbsolutePos = playerPos.move(direction)
        val nextTile = world.getGroundTileAbsolute(nextAbsolutePos)

        if (nextTile == Tile.OutOfSpace) {
            val isNetworkEvent = nextRoomId != null
            log.info { "Is Next Room a network event: $isNetworkEvent ($nextRoomId)" }
            val nextRoom = if (nextRoomId == null) worldFactory.discoverNextRoom(
                direction.opposite()
            ) else worldFactory.discoverNextRommById(nextRoomId)
            if (nextRoom != null) {
                addNewRoom(nextRoom, currentRoom, direction)
                nextRoom.removeExit(direction.opposite())
                return nextRoom
            } else {
                log.info { "Failed finding next room for direction: $direction" }
            }
        } else {
            log.info { "Next room location is already occupied: $nextAbsolutePos" }
        }
        return null
    }

    private fun addNewRoom(nextRoom: Room, currentRoom: Room, direction: Direction) {
        log.info { "Adding new room to map: ${nextRoom.id}" }
        nextRoom.offsetX = currentRoom.offsetX
        nextRoom.offsetY = currentRoom.offsetY
        when (direction) {
            Direction.Up -> {
                nextRoom.offsetY -= Room.size
            }
            Direction.Down -> {
                nextRoom.offsetY += Room.size
            }
            Direction.Left -> {
                nextRoom.offsetX -= Room.size
            }
            Direction.Right -> {
                nextRoom.offsetX += Room.size
            }
        }
        world.rooms += nextRoom
        worldComponent.addRoom(nextRoom)
    }

    fun addAllRooms() {
        var currentRoom: Room = world.getFirstRoom()
        val directions = listOf(
            Direction.Right,
            Direction.Right,
            Direction.Right,
            Direction.Down,
            Direction.Left,
            Direction.Left,
            Direction.Left,
            Direction.Down,
            Direction.Right,
            Direction.Right,
            Direction.Right,
            Direction.Right
        )
        directions.forEachIndexed { i, nextDirection ->
            worldFactory.discoverNextRoom()?.let { nextRoom ->
                addNewRoom(nextRoom, currentRoom, nextDirection)
                currentRoom = nextRoom
            }
        }
    }

}
