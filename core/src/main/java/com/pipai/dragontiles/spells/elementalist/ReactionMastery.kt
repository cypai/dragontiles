package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Cryo
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.status.Status

class ReactionMastery : PowerSpell() {
    override val id: String = "base:spells:ReactionMastery"
    override val requirement: ComponentRequirement = RainbowIdenticalSequence(3)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(9),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(ReactionMasteryImpl())
    }

    class ReactionMasteryImpl : Status(1) {
        override val assetName: String = "reaction_mastery.png"
        override val displayAmount: Boolean = false
        override val id: String = "base:status:ReactionMastery"

        override fun deepCopy(): Status {
            return ReactionMasteryImpl()
        }

        override fun queryForAdditionalFlags(
            origin: Combatant?,
            target: Combatant?,
            element: Element,
            flags: List<CombatFlag>
        ): List<CombatFlag> {

            return if (flags.contains(CombatFlag.ATTACK)) {
                when (element) {
                    Element.FIRE -> listOf(CombatFlag.PYRO)
                    Element.ICE -> listOf(CombatFlag.CRYO)
                    Element.LIGHTNING -> listOf(CombatFlag.ELECTRO)
                    else -> listOf()
                }
            } else {
                listOf()
            }
        }

        @CombatSubscribe
        suspend fun onAttack(ev: PlayerAttackEnemyEvent, api: CombatApi) {
            val reactant = when (ev.element) {
                Element.FIRE -> Pyro(1)
                Element.ICE -> Cryo(1)
                Element.LIGHTNING -> Electro(1)
                else -> return
            }
            api.addStatusToEnemy(ev.target, reactant)
        }

    }
}
