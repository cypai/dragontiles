package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*

class InflictReactant : StandardSpell() {
    override val id: String = "base:spells:InflictReactant"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(1),
    )
    override val additionalKeywords: List<String> = listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override fun flags(): List<CombatFlag> {
        val flag = reactantFlag(components())
        return if (flag == null) {
            listOf()
        } else {
            listOf(flag)
        }
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.addStatusToEnemy(target, reactant(components())!!)
    }
}
