package com.pipai.dragontiles.artemis.systems.animation

class DelayAnimation(private val delay: Float) : Animation() {

    override fun startAnimation() {
        endAnimation(delay)
    }
}
