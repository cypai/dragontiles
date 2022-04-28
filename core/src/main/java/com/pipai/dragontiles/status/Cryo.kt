package com.pipai.dragontiles.status

import com.pipai.dragontiles.artemis.systems.animation.NameTextAnimation
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.StringLocalized

class Cryo(amount: Int) : Status(amount) {
    override val id = "base:status:Cryo"
    override val assetName = "blue.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = true

    override fun deepCopy(): Status {
        return Cryo(amount)
    }

    override suspend fun onInflict(api: CombatApi) {
        when (val c = combatant) {
            is Combatant.EnemyCombatant -> {
                if (api.enemyHasStatus(c.enemy, Pyro::class)) {
                    api.animate(NameTextAnimation(Combatant.EnemyCombatant(c.enemy), StringLocalized("base:keywords:Melt")))
                    val pyroAmount = api.enemyStatusAmount(c.enemy, Pyro::class)
                    if (pyroAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Cryo::class)
                        api.addStatusToEnemy(c.enemy, Pyro(-theAmount))
                    } else {
                        api.removeEnemyStatus(c.enemy, Pyro::class)
                        api.addStatusToEnemy(c.enemy, Cryo(-pyroAmount))
                    }
                }
                if (api.enemyHasStatus(c.enemy, Electro::class)) {
                    api.animate(
                        NameTextAnimation(
                            Combatant.EnemyCombatant(c.enemy),
                            StringLocalized("base:keywords:Cryoshock")
                        )
                    )
                    val electroAmount = api.enemyStatusAmount(c.enemy, Electro::class)
                    if (electroAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Cryo::class)
                        api.addStatusToEnemy(c.enemy, Electro(-theAmount))
                        api.addStatusToEnemy(c.enemy, Cryoshock(theAmount))
                    } else {
                        api.removeEnemyStatus(c.enemy, Electro::class)
                        api.addStatusToEnemy(c.enemy, Cryo(-electroAmount))
                        api.addStatusToEnemy(c.enemy, Cryoshock(electroAmount))
                    }
                }
            }
            else -> {}
        }
    }

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float {
        return if (target == combatant && flags.any { it == CombatFlag.PYRO }) {
            2f
        } else {
            1f
        }
    }
}
