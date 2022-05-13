package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.spells.Rune

data class RuneChangeAnimation(val rune: Rune, val active: Boolean) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        val spellCard = sUi.getSpellCard(rune)
        if (spellCard != null) {
            if (active) {
                sUi.glowSpellCard(spellCard, rune.components())
            } else {
                sUi.glowSpellCard(spellCard, listOf())
            }
        }
        endAnimation()
    }

}
