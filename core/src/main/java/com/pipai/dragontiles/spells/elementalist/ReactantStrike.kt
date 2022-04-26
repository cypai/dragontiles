package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.utils.findAs
import com.pipai.dragontiles.utils.with

class ReactantStrike : StandardSpell() {
    override val id: String = "base:spells:ReactantStrike"
    override val requirement: ComponentRequirement = Sequential(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(6),
        FluxGainAspect(6),
        StackableAspect(GenericStatus(1), 1),
    )

    override fun additionalKeywords(): List<String> =
        listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override fun flags(): List<CombatFlag> {
        val flag = reactantFlag(components())
        return if (flag == null) {
            super.flags()
        } else {
            super.flags().with(flag)
        }
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.attack(target, elemental(components()), baseDamage(), flags())
        val reactant = reactant(components())!!
        reactant.amount = aspects.findAs(StackableAspect::class)!!.status.amount
        api.addStatusToEnemy(target, reactant)
    }
}