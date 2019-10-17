package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.enemies.Enemy

abstract class CountdownAttack(val id: Int) {
    abstract var attackPower: Int
    abstract var effectPower: Int
    abstract val element: Element
    abstract val discardSuit: Suit
    abstract var turnsLeft: Int
    abstract val name: String
    abstract val description: String?

    var counteredAttackPower: Int = 0
    var counteredEffectPower: Int = 0

    open suspend fun activateEffects(api: CombatApi) {
    }

    fun isDamaging() = attackPower - counteredAttackPower > 0
    fun calcAttackPower() = attackPower - counteredAttackPower
    fun calcEffectPower() = effectPower - counteredEffectPower
}

class StandardCountdownAttack(id: Int,
                              override var attackPower: Int,
                              override val element: Element,
                              override val discardSuit: Suit,
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
}

class BuffCountdownAttack(id: Int,
                          override var attackPower: Int,
                          override var effectPower: Int,
                          val statusAmount: List<Pair<Status, Int>>,
                          val targets: List<Enemy>,
                          override val element: Element,
                          override val discardSuit: Suit,
                          override val name: String,
                          override val description: String?,
                          override var turnsLeft: Int) : CountdownAttack(id) {
    constructor(id: Int, effectPower: Int, status: Status, amount: Int,
                target: Enemy, element: Element, discardSuit: Suit,
                name: String, description: String?, turnsLeft: Int)
            : this(id, 0, effectPower, listOf(Pair(status, amount)), listOf(target), element, discardSuit, name, description, turnsLeft)

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
                            override val discardSuit: Suit,
                            override val name: String,
                            override val description: String?,
                            override var turnsLeft: Int) : CountdownAttack(id) {

    override suspend fun activateEffects(api: CombatApi) {
        statusAmount.forEach { (status, amount) ->
            api.changeStatusIncrement(status, amount)
        }
    }
}
