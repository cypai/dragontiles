package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem

class PlayerDamageAnimation(private val damage: Int) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.setHpRelative(-damage)
        endAnimation()
    }

}
