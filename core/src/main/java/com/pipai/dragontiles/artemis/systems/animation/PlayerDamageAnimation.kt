package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem

class PlayerDamageAnimation(private val damage: Int) : Animation() {

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.changeHeroHp(-damage)
        sTop.setHpRelative(-damage)
        endAnimation()
    }

}
