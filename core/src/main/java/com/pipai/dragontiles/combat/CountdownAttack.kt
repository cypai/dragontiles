package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.choose
import java.util.*

abstract class CountdownAttack(val id: Int) : Targetable {
    abstract var attackPower: Int
    abstract var effectPower: Int
    abstract val element: Element
    abstract var turnsLeft: Int
    abstract val name: String
    abstract val description: String?

    var counteredAttackPower: Int = 0
    var counteredEffectPower: Int = 0

    open suspend fun activateEffects(api: CombatApi) {
    }

    abstract fun discardTile(rng: Random): Tile

    fun isDamaging() = attackPower - counteredAttackPower > 0
    fun calcAttackPower() = attackPower - counteredAttackPower
    fun calcEffectPower() = effectPower - counteredEffectPower
}

fun standardDiscard(rng: Random, suit: Suit): Tile {
    return when (suit) {
        Suit.FIRE -> Tile.ElementalTile(Suit.FIRE, rng.nextInt(9) + 1)
        Suit.ICE -> Tile.ElementalTile(Suit.ICE, rng.nextInt(9) + 1)
        Suit.LIGHTNING -> Tile.ElementalTile(Suit.LIGHTNING, rng.nextInt(9) + 1)
        Suit.STAR -> Tile.StarTile(StarType.values().choose(rng))
        Suit.LIFE -> Tile.LifeTile(LifeType.values().choose(rng))
    }
}

class StandardCountdownAttack(id: Int,
                              override var attackPower: Int,
                              override val element: Element,
                              val discardSuit: Suit,
                              override val name: String,
                              override val description: String?,
                              override var turnsLeft: Int) : CountdownAttack(id) {
    constructor(id: Int,
                attackPower: Int,
                element: Element,
                discardSuit: Suit,
                name: String,
                turnsLeft: Int)
            : this(id, attackPower, element, discardSuit, name, null, turnsLeft)

    override var effectPower: Int = 0

    override fun discardTile(rng: Random): Tile = standardDiscard(rng, discardSuit)
}

class BuffCountdownAttack(id: Int,
                          override var attackPower: Int,
                          override var effectPower: Int,
                          val statusAmount: List<Pair<Status, Int>>,
                          val targets: List<Enemy>,
                          override val element: Element,
                          val discardSuit: Suit,
                          override val name: String,
                          override val description: String?,
                          override var turnsLeft: Int) : CountdownAttack(id) {
    constructor(id: Int, effectPower: Int, status: Status, amount: Int,
                target: Enemy, element: Element, discardSuit: Suit,
                name: String, description: String?, turnsLeft: Int)
            : this(id, 0, effectPower, listOf(Pair(status, amount)), listOf(target), element, discardSuit, name, description, turnsLeft)

    override fun discardTile(rng: Random): Tile = standardDiscard(rng, discardSuit)

    override suspend fun activateEffects(api: CombatApi) {
        statusAmount.forEach { (status, amount) ->
            targets.forEach { target ->
                if (target.hp > 0) {
                    api.changeEnemyStatusIncrement(target.id, status, amount)
                }
            }
        }
    }
}

class DebuffCountdownAttack(id: Int,
                            override var attackPower: Int,
                            override var effectPower: Int,
                            val statusAmount: List<Pair<Status, Int>>,
                            override val element: Element,
                            val discardSuit: Suit,
                            override val name: String,
                            override val description: String?,
                            override var turnsLeft: Int) : CountdownAttack(id) {

    override fun discardTile(rng: Random): Tile = standardDiscard(rng, discardSuit)

    override suspend fun activateEffects(api: CombatApi) {
        statusAmount.forEach { (status, amount) ->
            api.changeStatusIncrement(status, amount)
        }
    }
}
