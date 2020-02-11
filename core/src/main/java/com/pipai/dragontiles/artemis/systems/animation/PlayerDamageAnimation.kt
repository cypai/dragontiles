package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem

class PlayerDamageAnimation(private val damage: Int) : Animation() {

    private lateinit var sTop: TopRowUiSystem

    override fun startAnimation() {
        sTop.setHpRelative(-damage)
        endAnimation()
    }

}
