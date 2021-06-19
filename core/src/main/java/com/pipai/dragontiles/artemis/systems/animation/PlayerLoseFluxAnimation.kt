package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem

class PlayerLoseFluxAnimation(private val amount: Int) : Animation() {

    private lateinit var sTop: TopRowUiSystem

    override fun startAnimation() {
        sTop.setFluxRelative(-amount)
        endAnimation()
    }

}
