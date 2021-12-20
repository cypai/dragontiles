package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.utils.getLogger

class BatchAnimation : Animation(), AnimationObserver {

    private val logger = getLogger()

    private val animations: MutableList<Animation> = mutableListOf()

    override fun startAnimation() {
        animations.forEach {
            logger.info("Start batch animation: $it")
            it.init(this.world, this.game)
            it.initObserver(this)
            it.startAnimation()
        }
    }

    fun addToBatch(animation: Animation) {
        animations.add(animation)
    }

    override fun notify(animation: Animation) {
        animations.remove(animation)
        if (animations.isEmpty()) {
            endAnimation()
        }
    }

}
