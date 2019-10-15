package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatQueryUiSystem
import com.pipai.dragontiles.combat.QueryTileOptionsEvent

data class QueryTileOptionsAnimation(val ev: QueryTileOptionsEvent) : Animation() {

    private lateinit var sQueryUi: CombatQueryUiSystem

    override fun startAnimation() {
        sQueryUi.queryTileOptions(ev)
        endAnimation()
    }

}
