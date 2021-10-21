package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.QueryTileOptionsEvent

data class QueryTileOptionsAnimation(val ev: QueryTileOptionsEvent) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.queryTileOptions(ev)
        endAnimation()
    }

}
