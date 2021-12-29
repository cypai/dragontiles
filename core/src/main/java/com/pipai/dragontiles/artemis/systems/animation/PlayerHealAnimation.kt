package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem

class PlayerHealAnimation(private val amount: Int) : Animation() {

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.changeHeroHp(amount)
        sTop.setHpRelative(amount)
        endAnimation()
    }

}
