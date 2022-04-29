package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getStackableCopy

class ColdHands : StandardSpell() {
    override val id: String = "base:spells:ColdHands"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.ICE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE_ENEMY
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(2),
        StackableAspect(Weak(3, false), 1),
        FluxGainAspect(2),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, Element.ICE, baseDamage(), flags())
        api.addStatusToEnemy(api.getEnemy(params.targets.first()), aspects.getStackableCopy(Weak::class))
        api.inflictTileStatusOnHand(
            RandomTileStatusInflictStrategy(
                TileStatus.FREEZE,
                1
            )
        )
    }
}
