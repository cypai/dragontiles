package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*

class IceShard : StandardSpell() {
    override val id: String = "base:spells:IceShard"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ICE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val glowType: GlowType = GlowType.ICE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(15),
        FluxGainAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        val hand = api.getHandTiles()
        if (hand.size > 1) {
            val tile = api.queryTiles("Pick a tile to freeze", hand, 1, 1)
            api.setTileStatus(tile, TileStatus.FREEZE)
        } else {
            api.setTileStatus(hand, TileStatus.FREEZE)
        }
    }
}
