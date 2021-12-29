package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.withAll

class PhoenixFire : StandardSpell() {
    override val id: String = "base:spells:PhoenixFire"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(16),
        FluxGainAspect(5),
        StackableAspect(Pyro(1), 1),
    )

    override fun additionalKeywords(): List<String> = listOf("@Reaction", "@Melt", "@Pyroblast")

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.PYRO))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
    }
}
