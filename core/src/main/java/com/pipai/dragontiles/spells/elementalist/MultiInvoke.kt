package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*

class MultiInvoke : StandardSpell() {
    override val id: String = "base:spells:MultiInvoke"
    override val requirement: ComponentRequirement = SequentialX()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(1),
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        val x = components().size
        repeat(x) {
            api.attack(target, elemental(components()), baseDamage())
        }
    }
}
