package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class Spark : StandardSpell() {
    override val id: String = "base:spells:Spark"
    override val requirement: ComponentRequirement = SinglePredicate(
        { it.tile.let { t -> t is Tile.ElementalTile && t.number == 1 } },
        SuitGroup.ELEMENTAL
    )
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2),
        RepeatableAspect(),
        FluxGainAspect(2),
    )

    override fun additionalKeywords(): List<String> = listOf("@Components")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
    }
}
