package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatQueryUiSystem
import com.pipai.dragontiles.artemis.systems.ui.MapUiSystem

class BattleWinAnimation : Animation() {
    private lateinit var sQueryUi: CombatQueryUiSystem
    private lateinit var sMap: MapUiSystem

    override fun startAnimation() {
        sQueryUi.generateRewards()
        sMap.canAdvanceMap = true
        endAnimation()
    }
}
