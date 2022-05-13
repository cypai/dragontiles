package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withoutAll

class PhoenixFire : StandardSpell() {
    override val id: String = "base:spells:PhoenixFire"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val glowType: GlowType = GlowType.FIRE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(17),
        FluxGainAspect(9),
    )

    override fun dynamicBaseDamage(components: List<TileInstance>, api: CombatApi): Int {
        val burns = api.getHandTiles()
            .withoutAll(components)
            .filter { it.tileStatus == TileStatus.BURN }
            .size
        return baseDamage() + (6 * burns)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(Element.FIRE, dynamicBaseDamage(components(), api), flags())
    }
}
