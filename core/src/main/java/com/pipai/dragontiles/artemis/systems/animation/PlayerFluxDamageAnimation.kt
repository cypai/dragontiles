package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem

class PlayerFluxDamageAnimation(private val damage: Int) : Animation() {

    private lateinit var sTop: TopRowUiSystem

    override fun startAnimation() {
        sTop.setFluxRelative(damage)
        endAnimation()
    }

}
