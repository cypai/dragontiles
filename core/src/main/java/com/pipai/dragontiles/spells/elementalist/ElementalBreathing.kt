package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.GenericStatus
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.findAs

class ElementalBreathing : PowerSpell() {
    override val id: String = "base:spells:ElementalBreathing"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        StackableAspect(GenericStatus(1), 1),
        FluxGainAspect(3),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val amount = aspects.findAs(StackableAspect::class)!!.status.amount
        val breathType = when (elemental(components())) {
            Element.FIRE -> ElementalBreathingStatus(
                amount,
                TileStatus.BURN,
                "base:status:FireBreathing",
                "red.png"
            )
            Element.ICE -> ElementalBreathingStatus(
                amount,
                TileStatus.FREEZE,
                "base:status:IceBreathing",
                "blue.png"
            )
            Element.LIGHTNING -> ElementalBreathingStatus(
                amount,
                TileStatus.SHOCK,
                "base:status:LightningBreathing",
                "yellow.png"
            )
            else -> {
                throw IllegalStateException("Unexpected element")
            }
        }
        api.addStatusToHero(breathType)
    }

    class ElementalBreathingStatus(amount: Int, private val tileStatus: TileStatus, id: String, assetName: String) :
        SimpleStatus(id, assetName, true, amount) {

        @CombatSubscribe
        suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
            if (api.combat.hand.isNotEmpty()) {
                if (api.combat.hand.size > 1) {
                    api.inflictTileStatusOnHand(
                        RandomTileStatusInflictStrategy(
                            tileStatus,
                            amount
                        )
                    )
                }
            }
        }
    }
}
