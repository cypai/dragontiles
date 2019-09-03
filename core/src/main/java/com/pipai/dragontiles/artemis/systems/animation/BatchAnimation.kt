package com.pipai.dragontiles.artemis.systems.animation

class BatchAnimation : Animation(), AnimationObserver {

    private val animations: MutableList<Animation> = mutableListOf()

    override fun startAnimation() {
        animations.forEach {
            it.startAnimation()
        }
    }

    fun addToBatch(animation: Animation) {
        animation.observer = this
        animations.add(animation)
    }

    override fun notify(animation: Animation) {
        animations.remove(animation)
        if (animations.isEmpty()) {
            observer.notify(this)
        }
    }

}
