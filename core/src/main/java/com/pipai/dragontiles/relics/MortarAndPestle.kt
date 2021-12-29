package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.data.Reward
import com.pipai.dragontiles.data.RewardGenerator
import com.pipai.dragontiles.spells.Rarity

class MortarAndPestle : Relic() {
    override val id = "base:relics:MortarAndPestle"
    override val assetName = "mortar_and_pestle.png"
    override val rarity = Rarity.RARE
    override val showCounter: Boolean = false

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        if (ev.turnNumber == 1 && api.runData.combatRewards.none { it is Reward.PotionReward }) {
            api.runData.combatRewards.add(
                Reward.PotionReward(
                    RewardGenerator().choosePotion(
                        api.gameData,
                        api.runData.seed.rewardRng()
                    ).id
                )
            )
        }
    }
}
