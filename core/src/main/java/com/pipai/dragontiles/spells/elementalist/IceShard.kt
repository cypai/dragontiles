package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Cryo
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.getStackableCopy

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
    override val additionalKeywords: List<String> = listOf("@Reaction", "@Melt", "@Cryoshock")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        api.addStatusToEnemy(target, aspects.getStackableCopy(Cryo::class))
        api.inflictTileStatusOnHand(RandomTileStatusInflictStrategy(TileStatus.FREEZE, 1))
    }
}
