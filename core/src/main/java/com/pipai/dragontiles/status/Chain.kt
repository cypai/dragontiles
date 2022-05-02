package com.pipai.dragontiles.status

import com.pipai.dragontiles.artemis.systems.animation.NameTextAnimation
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.SpellType

class Chain(amount: Int, private val enpoweredElement: Element) : SimpleStatus(
    when (enpoweredElement) {
        Element.FIRE -> "base:status:FireChain"
        Element.ICE -> "base:status:IceChain"
        Element.LIGHTNING -> "base:status:LightningChain"
        Element.NONE -> throw NotImplementedError()
    },
    when (enpoweredElement) {
        Element.FIRE -> "red.png"
        Element.ICE -> "blue.png"
        Element.LIGHTNING -> "yellow.png"
        Element.NONE -> throw NotImplementedError()
    },
    true,
    amount,
) {

    private var lastAttackElement: Element? = null

    override fun deepCopy(): Status {
        return Chain(amount, enpoweredElement)
    }

    @CombatSubscribe
    fun onAttack(ev: PlayerAttackEnemyEvent, api: CombatApi) {
        if (CombatFlag.CHAIN !in ev.flags) {
            lastAttackElement = ev.element
        }
    }

    @CombatSubscribe
    suspend fun onAttackCast(ev: SpellCastedEvent, api: CombatApi) {
        if (ev.spell.type == SpellType.ATTACK && lastAttackElement == enpoweredElement) {
            api.animate(NameTextAnimation(combatant!!, this))
            repeat (amount) {
                api.attack(
                    (combatant as Combatant.EnemyCombatant).enemy,
                    enpoweredElement,
                    1,
                    listOf(CombatFlag.ATTACK, CombatFlag.CHAIN)
                )
            }
        }
    }

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        api.removeHeroStatus(id)
    }
}
