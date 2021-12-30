package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.combat.TileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.getStackableCopy
import com.pipai.dragontiles.utils.withAll

class Fireball : StandardSpell() {
    override val id: String = "base:spells:Fireball"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.FIRE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(16),
        FluxGainAspect(8),
        StackableAspect(Pyro(1), 1),
    )

    override fun additionalKeywords(): List<String> = listOf("@Reaction", "@Melt", "@Pyroblast")

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.PYRO))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        api.addStatusToEnemy(target, aspects.getStackableCopy(Pyro::class))
        if (api.combat.hand.isNotEmpty()) {
            if (api.combat.hand.size > 1) {
                val tile = api.queryTiles("Pick a tile to burn", api.combat.hand, 1, 1)
                api.setTileStatus(tile, TileStatus.BURN)
            } else {
                api.setTileStatus(api.combat.hand, TileStatus.BURN)
            }
        }
    }
}
