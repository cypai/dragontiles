package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.QuerySwapEvent

data class QuerySwapAnimation(val ev: QuerySwapEvent) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.querySwap(ev)
        endAnimation()
    }

}
