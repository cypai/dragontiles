package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.BaseSystem
import com.pipai.dragontiles.artemis.systems.animation.Animation
import com.pipai.dragontiles.artemis.systems.animation.AnimationObserver

class CombatAnimationSystem : BaseSystem(), AnimationObserver {

    private var animating = false
    private val animationQueue: MutableList<Animation> = mutableListOf()

    override fun processSystem() {
        if (!animating && animationQueue.isNotEmpty()) {
            animationQueue.first().startAnimation()
            animating = true
        }
    }

    fun queueAnimation(animation: Animation) {
        animation.observer = this
        animationQueue.add(animation)
    }

    override fun notify(animation: Animation) {
        animationQueue.remove(animation)
        animating = false
    }

}
