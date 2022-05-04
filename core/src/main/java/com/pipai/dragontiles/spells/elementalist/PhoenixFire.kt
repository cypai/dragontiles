package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class PhoenixFire : StandardSpell() {
    override val id: String = "base:spells:PhoenixFire"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(17),
        FluxGainAspect(9),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
        val hand = api.getHandTiles()
        if (hand.size > 1) {
            val tile = api.queryTiles("Pick a tile to burn", hand, 1, 1)
            api.setTileStatus(tile, TileStatus.BURN)
        } else {
            api.setTileStatus(hand, TileStatus.BURN)
        }
    }
}
