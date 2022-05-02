package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.artemis.systems.ui.*

class BattleWinAnimation : Animation() {
    private lateinit var sRewards: RewardsSystem
    private lateinit var sCombat: CombatControllerSystem
    private lateinit var sUi: CombatUiSystem
    private lateinit var sDeckUi: DeckDisplayUiSystem
    private lateinit var sMap: MapUiSystem
    private lateinit var sPause: PauseMenuSystem
    private lateinit var sStatus: StatusSystem
    private lateinit var sAnimation: CombatAnimationSystem

    override fun startAnimation() {
        sCombat.runData.adjustPostCombat(sCombat.combat)
        sDeckUi.enableSwap = true
        sUi.disable()
        sRewards.activateRewards()
        sMap.canAdvanceMap = true
        sPause.enable()
        sStatus.clearHeroStatuses()
        sAnimation.isEnabled = false
        endAnimation()
    }
}
