package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.status.Status

interface Intent {
    val enemy: Enemy
    suspend fun execute(api: CombatApi)
}

data class AttackIntent(
    override val enemy: Enemy, val attackPower: Int, val multistrike: Int, val piercing: Boolean, val element: Element
) : Intent {

    fun calculateDamage(api: CombatApi): Int {
        return api.calculateDamageOnHero(enemy, element, attackPower)
    }

    override suspend fun execute(api: CombatApi) {
        repeat(multistrike) {
            val damage = calculateDamage(api)
            api.dealDamageToHero(damage)
        }
    }
}

data class BuffIntent(
    override val enemy: Enemy, val status: Status, val attackIntent: AttackIntent?
) : Intent {

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        api.addStatusToEnemy(enemy, status)
    }
}

data class DebuffIntent(
    override val enemy: Enemy, val status: Status, val attackIntent: AttackIntent?
) : Intent {

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        api.addStatusToHero(status)
    }
}
