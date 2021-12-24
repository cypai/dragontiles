package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.SwapEvent
import com.pipai.dragontiles.spells.Spell

class SwapAnimation(private val ev: SwapEvent) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.swapSpells(ev.activeIndexes, ev.sideboardIndexes)
        endAnimation()
    }

}
