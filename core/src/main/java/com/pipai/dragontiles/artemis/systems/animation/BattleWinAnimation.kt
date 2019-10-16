package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatQueryUiSystem

class BattleWinAnimation : Animation() {
    private lateinit var sQueryUi: CombatQueryUiSystem

    override fun startAnimation() {
        sQueryUi.generateRewards()
        endAnimation()
    }
}
