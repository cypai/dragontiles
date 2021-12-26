package com.pipai.dragontiles.data

enum class Suit(val order: Int) {
    FUMBLE(0), FIRE(1), ICE(2), LIGHTNING(3), LIFE(4), STAR(5)
}

enum class Element(val isElemental: Boolean) {
    FIRE(true), ICE(true), LIGHTNING(true), NONE(false)
}

enum class StarType(val order: Int) {
    EARTH(1), MOON(2), SUN(3), STAR(4)
}

enum class LifeType(val order: Int) {
    LIFE(1), MIND(2), SOUL(3)
}

enum class TileStatus {
    NONE, BURN, FREEZE, SHOCK, VOLATILE, CURSE,
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

    class FumbleTile : Tile() {
        override var suit = Suit.FUMBLE
        override fun order(): Int = 0
    }
}

val fireTiles = (1..9).map { Tile.ElementalTile(Suit.FIRE, it) }
val iceTiles = (1..9).map { Tile.ElementalTile(Suit.ICE, it) }
val lightningTiles = (1..9).map { Tile.ElementalTile(Suit.LIGHTNING, it) }
val starTiles = StarType.values().map { Tile.StarTile(it) }
val lifeTiles = LifeType.values().map { Tile.LifeTile(it) }

data class TileInstance(val tile: Tile, var tileStatus: TileStatus, val id: Int)

fun terminal(tile: Tile, hand: List<Tile>): Boolean {
    if (hand.count { it == tile } > 1) {
        return false
    }
    return when (tile) {
        is Tile.ElementalTile -> {
            hand.none { (tile.number < 9 && it == successor(tile)) && (tile.number > 1 && it == predecessor(tile)) }
        }
        else -> {
            true
        }
    }
}

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
        is Tile.FumbleTile -> Tile.FumbleTile()
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
        is Tile.FumbleTile -> Tile.FumbleTile()
    }
}
