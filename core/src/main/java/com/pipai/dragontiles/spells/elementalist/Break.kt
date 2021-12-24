package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.utils.findAsWhere

class Break : StandardSpell() {
    override val id: String = "base:spells:Break"
    override val requirement: ComponentRequirement = Identical(3)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.STARTER
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(BreakStatus(3, false), 1),
        FluxGainAspect(2)
    )

    override fun additionalLocalized(): List<String> = listOf(BreakStatus(0, false).id)

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val stackable = aspects.findAsWhere(StackableAspect::class) { it.status is BreakStatus }!!
        api.addStatusToEnemy(api.getEnemy(params.targets.first()), stackable.status.deepCopy())
    }
}
