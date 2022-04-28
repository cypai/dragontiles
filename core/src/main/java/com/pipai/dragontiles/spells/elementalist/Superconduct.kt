package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Cryoshock

class Superconduct : StandardSpell() {
    override val id: String = "base:spells:Superconduct"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ICE_LIGHTNING)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(8),
        ExhaustAspect(),
    )

    override fun additionalKeywords(): List<String> =
        listOf("@Cryoshock")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.addStatusToEnemy(target, Cryoshock(api.enemyStatusAmount(target, Cryoshock::class)))
    }
}
