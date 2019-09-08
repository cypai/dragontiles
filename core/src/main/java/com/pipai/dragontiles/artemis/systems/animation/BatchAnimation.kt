package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World

class BatchAnimation(world: World) : Animation(world), AnimationObserver {

    private val animations: MutableList<Animation> = mutableListOf()

    override fun startAnimation() {
        animations.forEach {
            it.startAnimation()
        }
    }

    fun addToBatch(animation: Animation) {
        animation.init(this)
        animations.add(animation)
    }

    override fun notify(animation: Animation) {
        animations.remove(animation)
        if (animations.isEmpty()) {
            endAnimation()
        }
    }

}
