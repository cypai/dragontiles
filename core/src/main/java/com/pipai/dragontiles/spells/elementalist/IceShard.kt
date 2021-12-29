package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.combat.TileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Cryo
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.getStackableCopy
import com.pipai.dragontiles.utils.withAll

class IceShard : StandardSpell() {
    override val id: String = "base:spells:IceShard"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ICE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(13),
        FluxGainAspect(2),
        StackableAspect(Cryo(1), 1),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.CRYO))
    }

    override fun additionalKeywords(): List<String> = listOf("@Reaction", "@Melt", "@Cryoshock")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        api.addStatusToEnemy(target, aspects.getStackableCopy(Cryo::class))
        if (api.combat.hand.isNotEmpty()) {
            if (api.combat.hand.size > 1) {
                val tile = api.queryTiles("Pick a tile to freeze", api.combat.hand, 1, 1)
                api.setTileStatus(tile, TileStatus.FREEZE)
            } else {
                api.setTileStatus(api.combat.hand, TileStatus.FREEZE)
            }
        }
    }
}
