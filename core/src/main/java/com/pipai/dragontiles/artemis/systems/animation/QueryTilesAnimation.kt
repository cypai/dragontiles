package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatQueryUiSystem
import com.pipai.dragontiles.combat.QueryTilesEvent

data class QueryTilesAnimation(val ev: QueryTilesEvent) : Animation() {

    private lateinit var sQueryUi: CombatQueryUiSystem

    override fun startAnimation() {
        sQueryUi.queryTiles(ev)
        endAnimation()
    }

}
