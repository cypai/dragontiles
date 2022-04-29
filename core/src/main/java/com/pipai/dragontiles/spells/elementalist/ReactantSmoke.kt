package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.utils.findAs
import com.pipai.dragontiles.utils.with

class ReactantSmoke : StandardSpell() {
    override val id: String = "base:spells:ReactantSmoke"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(3), 1),
        FluxGainAspect(6),
    )

    override fun additionalKeywords(): List<String> =
        listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override fun flags(): List<CombatFlag> {
        val flag = reactantFlag(components())
        return if (flag == null) {
            super.flags()
        } else {
            super.flags().with(flag)
        }
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val reactant = reactant(components())!!
        reactant.amount = aspects.findAs(StackableAspect::class)!!.status.amount
        api.addAoeStatus(reactant)
    }
}
