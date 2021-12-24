package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.artemis.systems.ui.MapUiSystem
import com.pipai.dragontiles.artemis.systems.ui.RewardsSystem

class BattleWinAnimation : Animation() {
    private lateinit var sRewards: RewardsSystem
    private lateinit var sCombat: CombatControllerSystem
    private lateinit var sUi: CombatUiSystem
    private lateinit var sMap: MapUiSystem

    override fun startAnimation() {
        sCombat.runData.adjustPostCombat(sCombat.combat)
        sUi.disable()
        sRewards.activateRewards()
        sMap.canAdvanceMap = true
        endAnimation()
    }
}
