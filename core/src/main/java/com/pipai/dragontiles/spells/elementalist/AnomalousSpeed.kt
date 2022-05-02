package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.choose

class AnomalousSpeed : PowerSpell() {
    override val id: String = "base:spells:AnomalousSpeed"
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.LIFE)
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(4),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(AnomalousSpeedStatus(1))
    }

    class AnomalousSpeedStatus(amount: Int) : SimpleStatus("base:status:AnomalousSpeed", "gray.png", true, amount) {
        private var playerTurn = true

        @CombatSubscribe
        suspend fun onStartTurn(ev: TurnStartEvent, api: CombatApi) {
            playerTurn = true
        }

        @CombatSubscribe
        suspend fun onEndTurn(ev: TurnEndEvent, api: CombatApi) {
            playerTurn = false
        }

        @CombatSubscribe
        suspend fun onTileStatus(ev: TileStatusChangeEvent, api: CombatApi) {
            if (playerTurn && ev.tileStatus in listOf(TileStatus.BURN, TileStatus.FREEZE, TileStatus.SHOCK)) {
                api.draw(ev.tiles.size)
            }
        }
    }
}
