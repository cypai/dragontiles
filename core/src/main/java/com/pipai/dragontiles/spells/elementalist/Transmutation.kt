package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.findAs

class Transmutation : Rune() {
    override val id: String = "base:spells:Transmutation"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val requirement: ComponentRequirement = ForbidTransformFreeze(
        this,
        AnyCombo(3, SuitGroup.ELEMENTAL),
    )
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TransformAspect(),
        NotManuallyDeactivateable(),
        PreserveComponentOrder(),
        FluxGainAspect(9),
    )

    override suspend fun onDeactivate(api: CombatApi) {
        val componentList = components().toMutableList()
        val first = componentList.removeAt(0).tile as Tile.ElementalTile
        componentList.forEach {
            val tile = it.tile as Tile.ElementalTile
            api.transformTile(it, Tile.ElementalTile(first.suit, tile.number), false)
        }
        api.sortHand()
    }

    @CombatSubscribe
    suspend fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        this.deactivate(api)
    }

}
