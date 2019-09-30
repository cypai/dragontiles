package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem

class PlayerDamageAnimation(private val damage: Int,
                            private val hp: Int,
                            private val hpMax: Int) : Animation() {

    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sUi.setHp(hp, hpMax)
        endAnimation()
    }

}
