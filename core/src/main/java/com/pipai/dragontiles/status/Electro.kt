package com.pipai.dragontiles.status

import com.pipai.dragontiles.artemis.systems.animation.NameTextAnimation
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.StringLocalized

class Electro(amount: Int) : Status(amount) {
    override val id = "base:status:Electro"
    override val assetName = "yellow.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = true

    override fun deepCopy(): Status {
        return Electro(amount)
    }

    override suspend fun onInflict(api: CombatApi) {
        when (val c = combatant) {
            is Combatant.EnemyCombatant -> {
                if (api.enemyHasStatus(c.enemy, Pyro::class)) {
                    api.animate(
                        NameTextAnimation(
                            Combatant.EnemyCombatant(c.enemy),
                            StringLocalized("base:keywords:Pyroblast")
                        )
                    )
                    val pyroAmount = api.enemyStatusAmount(c.enemy, Pyro::class)
                    if (pyroAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Electro::class)
                        api.addStatusToEnemy(c.enemy, Pyro(-theAmount))
                        api.aoeAttack(Element.FIRE, 5 * theAmount, listOf())
                    } else {
                        api.removeEnemyStatus(c.enemy, Pyro::class)
                        api.addStatusToEnemy(c.enemy, Electro(-pyroAmount))
                        api.aoeAttack(Element.FIRE, 5 * pyroAmount, listOf())
                    }
                }
                if (api.enemyHasStatus(c.enemy, Cryo::class)) {
                    api.animate(
                        NameTextAnimation(
                            Combatant.EnemyCombatant(c.enemy),
                            StringLocalized("base:keywords:Cryoshock")
                        )
                    )
                    val cryoAmount = api.enemyStatusAmount(c.enemy, Cryo::class)
                    if (cryoAmount >= amount) {
                        val theAmount = amount // cached because the api changes it
                        api.removeEnemyStatus(c.enemy, Electro::class)
                        api.addStatusToEnemy(c.enemy, Cryo(-theAmount))
                        api.addStatusToEnemy(c.enemy, Cryoshock(theAmount))
                    } else {
                        api.removeEnemyStatus(c.enemy, Cryo::class)
                        api.addStatusToEnemy(c.enemy, Electro(-cryoAmount))
                        api.addStatusToEnemy(c.enemy, Cryoshock(cryoAmount))
                    }
                }
            }
            else -> {}
        }
    }
}
