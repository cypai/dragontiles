package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.QueryTilesEvent

data class QueryTilesAnimation(val ev: QueryTilesEvent) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.queryTiles(ev)
        endAnimation()
    }

}
