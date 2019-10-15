package com.pipai.dragontiles.data

enum class Suit(val order: Int) {
    FIRE(1), ICE(2), LIGHTNING(3), STAR(4), LIFE(5)
}

enum class Element {
    FIRE, ICE, LIGHTNING, NONE
}

enum class StarType(val order: Int) {
    EARTH(1), MOON(2), SUN(3), STAR(4)
}

enum class LifeType(val order: Int) {
    LIFE(1), MIND(2), SOUL(3)
}

sealed class Tile {
    abstract var suit: Suit
    abstract fun order(): Int

    data class ElementalTile(override var suit: Suit, var number: Int) : Tile() {
        override fun order(): Int = number
    }

    data class StarTile(var type: StarType) : Tile() {
        override var suit = Suit.STAR
        override fun order(): Int = type.order
    }

    data class LifeTile(var type: LifeType) : Tile() {
        override var suit = Suit.LIFE
        override fun order(): Int = type.order
    }
}

val fireTiles = (1..9).map { Tile.ElementalTile(Suit.FIRE, it) }
val iceTiles = (1..9).map { Tile.ElementalTile(Suit.ICE, it) }
val lightningTiles = (1..9).map { Tile.ElementalTile(Suit.LIGHTNING, it) }
val starTiles = StarType.values().map { Tile.StarTile(it) }
val lifeTiles = LifeType.values().map { Tile.LifeTile(it) }

data class TileInstance(var tile: Tile, val id: Int)

fun successor(tile: Tile): Tile {
    return when (tile) {
        is Tile.ElementalTile -> Tile.ElementalTile(tile.suit, if (tile.number == 9) 1 else (tile.number + 1))
        is Tile.StarTile -> when (tile.type) {
            StarType.EARTH -> Tile.StarTile(StarType.MOON)
            StarType.MOON -> Tile.StarTile(StarType.SUN)
            StarType.SUN -> Tile.StarTile(StarType.STAR)
            StarType.STAR -> Tile.StarTile(StarType.EARTH)
        }
        is Tile.LifeTile -> when (tile.type) {
            LifeType.LIFE -> Tile.LifeTile(LifeType.MIND)
            LifeType.MIND -> Tile.LifeTile(LifeType.SOUL)
            LifeType.SOUL -> Tile.LifeTile(LifeType.LIFE)
        }
    }
}

fun predecessor(tile: Tile): Tile {
    return when (tile) {
        is Tile.ElementalTile -> Tile.ElementalTile(tile.suit, if (tile.number == 1) 9 else (tile.number - 1))
        is Tile.StarTile -> when (tile.type) {
            StarType.EARTH -> Tile.StarTile(StarType.STAR)
            StarType.MOON -> Tile.StarTile(StarType.EARTH)
            StarType.SUN -> Tile.StarTile(StarType.MOON)
            StarType.STAR -> Tile.StarTile(StarType.SUN)
        }
        is Tile.LifeTile -> when (tile.type) {
            LifeType.LIFE -> Tile.LifeTile(LifeType.SOUL)
            LifeType.MIND -> Tile.LifeTile(LifeType.LIFE)
            LifeType.SOUL -> Tile.LifeTile(LifeType.MIND)
        }
    }
}
