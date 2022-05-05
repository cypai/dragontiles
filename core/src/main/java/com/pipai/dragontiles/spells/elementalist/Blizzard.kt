package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withoutAll

class Blizzard : StandardSpell() {
    override val id: String = "base:spells:Blizzard"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ICE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(17),
        FluxGainAspect(9),
    )

    override fun dynamicBaseDamage(components: List<TileInstance>, api: CombatApi): Int {
        val freezes = api.getHandTiles()
            .withoutAll(components)
            .filter { it.tileStatus == TileStatus.FREEZE }
            .size
        return baseDamage() + (6 * freezes)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(Element.ICE, dynamicBaseDamage(components(), api), flags())
    }
}
