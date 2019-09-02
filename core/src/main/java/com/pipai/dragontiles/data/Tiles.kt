package com.pipai.dragontiles.data

enum class Suit {
    FIRE, ICE, LIGHTNING, STAR, LIFE
}

enum class Element {
    FIRE, ICE, LIGHTNING, NONE
}

enum class StarType {
    EARTH, MOON, SUN, STAR
}

enum class LifeType {
    LIFE, MIND, SOUL
}

sealed class Tile {
    abstract var suit: Suit

    data class ElementalTile(override var suit: Suit, var number: Int) : Tile()

    data class StarTile(var type: StarType) : Tile() {
        override var suit = Suit.STAR
    }

    data class LifeTile(var type: LifeType) : Tile() {
        override var suit = Suit.LIFE
    }
}