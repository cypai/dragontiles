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
    companion object {
        var nextId = 0
    }

    val id: Int
    abstract var suit: Suit
    abstract fun order(): Int

    init {
        id = nextId
        nextId++
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Tile -> {
                id == other.id
            }
            else -> {
                super.equals(other)
            }
        }
    }

    abstract fun valueEquals(other: Tile): Boolean

    override fun hashCode(): Int {
        return id
    }

    data class ElementalTile(override var suit: Suit, var number: Int) : Tile() {
        override fun order(): Int = number

        override fun toString(): String {
            return "ElementalTile(suit=$suit, number=$number, id=$id)"
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun valueEquals(other: Tile): Boolean {
            return other is ElementalTile && other.suit == suit && other.number == number
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    data class StarTile(var type: StarType) : Tile() {
        override var suit = Suit.STAR
        override fun order(): Int = type.order

        override fun toString(): String {
            return "StarTile(suit=$suit, type=$type, id=$id)"
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun valueEquals(other: Tile): Boolean {
            return other is StarTile && other.suit == suit && other.type == type
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    data class LifeTile(var type: LifeType) : Tile() {
        override var suit = Suit.LIFE
        override fun order(): Int = type.order

        override fun toString(): String {
            return "LifeTile(suit=$suit, type=$type, id=$id)"
        }

        override fun equals(other: Any?): Boolean {
            return super.equals(other)
        }

        override fun valueEquals(other: Tile): Boolean {
            return other is LifeTile && other.suit == suit && other.type == type
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }
}
