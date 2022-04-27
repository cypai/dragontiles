package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.utils.findAs

class ReactantGenesis : Sorcery() {
    override val id = "base:sorceries:ReactantGenesis"
    override val requirement = Identical(3, SuitGroup.ELEMENTAL)
    override val rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(3), 1),
    )

    override fun additionalKeywords(): List<String> =
        listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        val reactant = reactant(components())!!
        reactant.amount = aspects.findAs(StackableAspect::class)!!.status.amount
        api.addAoeStatus(reactant)
    }
}
