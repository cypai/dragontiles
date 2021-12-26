package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.combat.TileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.withAll

class Burn : StandardSpell() {
    override val id: String = "base:spells:Burn"
    override val requirement: ComponentRequirement = Single(SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(Pyro(1), 1),
        AttackDamageAspect(2),
        FluxGainAspect(1),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.PYRO, CombatFlag.PIERCING))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        api.inflictTileStatusOnHand(RandomTileStatusInflictStrategy(TileStatus.BURN, 1, TileStatusInflictStrategy.NotEnoughStrategy.RANDOM))
    }
}
