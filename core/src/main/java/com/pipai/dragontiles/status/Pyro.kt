package com.pipai.dragontiles.status

import com.pipai.dragontiles.artemis.systems.animation.NameTextAnimation
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.StringLocalized

class Pyro(amount: Int) : Status(amount) {
    override val id = "base:status:Pyro"
    override val assetName = "red.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = true

    override fun deepCopy(): Status {
        return Pyro(amount)
    }

    override suspend fun onInflict(api: CombatApi) {
        when (val c = combatant) {
            is Combatant.EnemyCombatant -> {
                if (api.enemyHasStatus(c.enemy, Cryo::class)) {
                    api.animate(NameTextAnimation(Combatant.EnemyCombatant(c.enemy), StringLocalized("base:keywords:Melt")))
                    val cryoAmount = api.enemyStatusAmount(c.enemy, Cryo::class)
                    if (cryoAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Pyro::class)
                        api.addStatusToEnemy(c.enemy, Cryo(-theAmount))
                    } else {
                        api.removeEnemyStatus(c.enemy, Cryo::class)
                        api.addStatusToEnemy(c.enemy, Pyro(-cryoAmount))
                    }
                }
                if (api.enemyHasStatus(c.enemy, Electro::class)) {
                    api.animate(
                        NameTextAnimation(
                            Combatant.EnemyCombatant(c.enemy),
                            StringLocalized("base:keywords:Pyroblast")
                        )
                    )
                    val electroAmount = api.enemyStatusAmount(c.enemy, Electro::class)
                    if (electroAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Pyro::class)
                        api.addStatusToEnemy(c.enemy, Electro(-theAmount))
                        api.aoeAttack(Element.FIRE, 5 * theAmount, listOf())
                    } else {
                        api.removeEnemyStatus(c.enemy, Electro::class)
                        api.addStatusToEnemy(c.enemy, Pyro(-electroAmount))
                        api.aoeAttack(Element.FIRE, 5 * electroAmount, listOf())
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
        return if (target == combatant && flags.any { it == CombatFlag.CRYO }) {
            2f
        } else {
            1f
        }
    }

}
