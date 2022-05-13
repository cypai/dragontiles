package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.withoutAll

class RapidFire : StandardSpell() {
    override val id: String = "base:spells:RapidFire"
    override val requirement: ComponentRequirement = Sequential(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.RARE
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(5),
        FluxGainAspect(9),
        ExhaustAspect(),
    )

    override fun dynamicCustomWithApi(param: String?, api: CombatApi): Int {
        val c = components()
        if (c.isEmpty()) {
            return 0
        }
        val suit = c.first().tile.suit
        return api.combat.hand.withoutAll(c)
            .filter { it.tile.suit == suit }
            .size
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val c = components()
        val suit = c.first().tile.suit
        val amount = api.combat.hand.withoutAll(c)
            .filter { it.tile.suit == suit }
            .size
        val target = api.getEnemy(params.targets.first())
        repeat(amount) {
            api.attack(target, elemental(components()), baseDamage(), flags())
        }
    }
}
