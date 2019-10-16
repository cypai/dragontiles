package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.enemies.Enemy

abstract class CountdownAttack(val id: Int) {
    abstract val attackPower: Int
    abstract val effectPower: Int
    abstract val element: Element
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
                              override val attackPower: Int,
                              override val element: Element,
                              override val name: String,
                              override var turnsLeft: Int) : CountdownAttack(id) {
    override val effectPower: Int = 0
    override val description: String? = null
}

class BuffCountdownAttack(id: Int,
                          override val attackPower: Int,
                          override val effectPower: Int,
                          val statusAmount: List<Pair<Status, Int>>,
                          val targets: List<Enemy>,
                          override val element: Element,
                          override val name: String,
                          override val description: String?,
                          override var turnsLeft: Int) : CountdownAttack(id) {

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
                            override val attackPower: Int,
                            override val effectPower: Int,
                            val statusAmount: List<Pair<Status, Int>>,
                            override val element: Element,
                            override val name: String,
                            override val description: String?,
                            override var turnsLeft: Int) : CountdownAttack(id) {

    override suspend fun activateEffects(api: CombatApi) {
        statusAmount.forEach { (status, amount) ->
            api.changeStatusIncrement(status, amount)
        }
    }
}
