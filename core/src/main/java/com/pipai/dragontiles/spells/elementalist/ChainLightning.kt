package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.utils.getStackableCopy
import com.pipai.dragontiles.utils.withAll

class ChainLightning : StandardSpell() {
    override val id: String = "base:spells:ChainLightning"
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.LIGHTNING)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(14),
        FluxGainAspect(4),
        StackableAspect(Electro(1), 1),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.ELECTRO))
    }

    override fun additionalKeywords(): List<String> = listOf("@Reaction", "@Pyroblast", "@Cryoshock")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.aoeAttack(elemental(components()), baseDamage(), flags())
        api.addAoeStatus(aspects.getStackableCopy(Electro::class))
        api.inflictTileStatusOnHand(RandomTileStatusInflictStrategy(TileStatus.SHOCK, 1))
    }
}
