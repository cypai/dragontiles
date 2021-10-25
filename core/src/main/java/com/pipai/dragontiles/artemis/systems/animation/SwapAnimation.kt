package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.spells.Spell

class SwapAnimation(private val spellInHand: List<Spell>,
                    private val spellOnSide: List<Spell>) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.swapSpells(spellInHand, spellOnSide)
        endAnimation()
    }

}
