package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withAll

class MultiInvoke : StandardSpell() {
    override val id: String = "base:spells:MultiInvoke"
    override val requirement: ComponentRequirement = SequentialX()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(1),
        FluxGainAspect(3),
        XAspect(0),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.INVOKE))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        repeat(x()) {
            api.attack(target, elemental(components()), baseDamage(), flags())
        }
    }
}
