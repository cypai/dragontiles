package com.pipai.dragontiles.artemis.systems.animation

class BatchAnimation : Animation(), AnimationObserver {

    private val animations: MutableList<Animation> = mutableListOf()

    override fun startAnimation() {
        animations.forEach {
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
