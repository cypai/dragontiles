package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.*
import com.pipai.dragontiles.utils.getStackableAmount

class SummonPhoenix : Sorcery() {
    override val id = "base:sorceries:SummonPhoenix"
    override val requirement = Sequential(9)
    override val rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(10), 1),
    )
    override val scoreable: Boolean = true

    override fun additionalKeywords(): List<String> =
        listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        when (elemental(components())) {
            Element.FIRE -> api.addStatusToHero(PyroPhoenix(aspects.getStackableAmount(GenericStatus::class)))
            Element.ICE -> api.addStatusToHero(CryoPhoenix(aspects.getStackableAmount(GenericStatus::class)))
            Element.LIGHTNING -> api.addStatusToHero(ElectroPhoenix(aspects.getStackableAmount(GenericStatus::class)))
            else -> {
            }
        }
    }

    class PyroPhoenix(amount: Int) :
        SimpleStatus("base:status:PyroPhoenix", "red.png", true, amount) {
        override fun deepCopy(): Status {
            return PyroPhoenix(amount)
        }

        @CombatSubscribe
        suspend fun onPlayerStartTurn(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Pyro(amount))
        }
    }

    class CryoPhoenix(amount: Int) :
        SimpleStatus("base:status:CryoPhoenix", "blue.png", true, 1) {
        override fun deepCopy(): Status {
            return CryoPhoenix(amount)
        }

        @CombatSubscribe
        suspend fun onPlayerStartTurn(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Cryo(amount))
        }
    }

    class ElectroPhoenix(amount: Int) :
        SimpleStatus("base:status:ElectroPhoenix", "yellow.png", true, 1) {
        override fun deepCopy(): Status {
            return ElectroPhoenix(amount)
        }

        @CombatSubscribe
        suspend fun onPlayerStartTurn(ev: TurnStartEvent, api: CombatApi) {
            api.addAoeStatus(Electro(amount))
        }
    }

}
