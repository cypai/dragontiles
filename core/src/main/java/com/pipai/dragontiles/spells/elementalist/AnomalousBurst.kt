package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class AnomalousBurst : StandardSpell() {
    override val id: String = "base:spells:AnomalousBurst"
    override val requirement: ComponentRequirement = SequentialX()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(4),
        FluxGainAspect(5),
        XAspect(1, 0, false),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val statusesAmount = components()
            .filter { it.tileStatus in listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK) }
            .size
        val target = api.getEnemy(params.targets.first())
        repeat(statusesAmount) {
            api.attack(target, elemental(components()), baseDamage(), flags())
        }
    }
}
