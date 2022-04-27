package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.spells.colorless.DragonScale
import com.pipai.dragontiles.status.*
import com.pipai.dragontiles.utils.findAs
import com.pipai.dragontiles.utils.with

class ReactantFumes : PowerSpell() {
    override val id: String = "base:spells:ReactantFumes"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ELEMENTAL)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(1), 1),
        FluxGainAspect(10),
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
        val amount = aspects.findAs(StackableAspect::class)!!.status.amount
        when (elemental(components())) {
            Element.FIRE -> api.addStatusToHero(PyroFumes(amount))
            Element.ICE -> api.addStatusToHero(CryoFumes(amount))
            Element.LIGHTNING -> api.addStatusToHero(ElectroFumes(amount))
            else -> {
            }
        }
    }

    class PyroFumes(amount: Int) :
        SimpleStatus("base:status:PyroFumes", "red.png", true, amount) {

        @CombatSubscribe
        suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Pyro(amount))
        }
    }

    class CryoFumes(amount: Int) :
        SimpleStatus("base:status:CryoFumes", "blue.png", true, amount) {

        @CombatSubscribe
        suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Cryo(amount))
        }
    }

    class ElectroFumes(amount: Int) :
        SimpleStatus("base:status:ElectroFumes", "yellow.png", true, amount) {

        @CombatSubscribe
        suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Electro(amount))
        }
    }
}
