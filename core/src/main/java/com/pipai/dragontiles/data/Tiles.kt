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

data class TileInstance(val tile: Tile, val id: Int)
