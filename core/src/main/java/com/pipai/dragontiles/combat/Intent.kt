package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element

interface Intent {
    val enemyId: Int
    suspend fun execute(api: CombatApi)
}

data class AttackIntent(
    override val enemyId: Int, val attackPower: Int, val multistrike: Int, val piercing: Boolean, val element: Element
) : Intent {

    fun calculateDamage(api: CombatApi): Int {
        return api.calculateActualDamage(
            DamageOrigin.ENEMY_ATTACK,
            DamageTarget.HERO,
            api.combat.enemyStatus[enemyId]!!,
            api.combat.heroStatus,
            element,
            attackPower
        )
    }

    override suspend fun execute(api: CombatApi) {
        repeat(multistrike) {
            val damage = calculateDamage(api)
            api.dealDamageToHero(damage)
        }
    }
}

data class BuffIntent(
    override val enemyId: Int, val status: Status, val amount: Int, val attackIntent: AttackIntent?
) : Intent {

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        api.changeEnemyStatusIncrement(enemyId, status, amount)
    }
}

data class DebuffIntent(
    override val enemyId: Int, val status: Status, val amount: Int, val attackIntent: AttackIntent?
) : Intent {

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        api.changeStatusIncrement(status, amount)
    }
}
