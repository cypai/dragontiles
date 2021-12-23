package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.QuerySwapEvent

data class QuerySwapAnimation(val amount: Int) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.querySwap(amount)
        endAnimation()
    }

}
