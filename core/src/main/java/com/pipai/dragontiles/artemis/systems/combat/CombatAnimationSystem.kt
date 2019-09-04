package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.BaseSystem
import com.pipai.dragontiles.artemis.systems.animation.Animation
import com.pipai.dragontiles.artemis.systems.animation.AnimationObserver
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.utils.system

class CombatAnimationSystem : BaseSystem(), AnimationObserver {

    private var animating = false
    private val animationQueue: MutableList<Animation> = mutableListOf()

    private val sUi by system<CombatUiSystem>()

    override fun processSystem() {
        if (!animating && animationQueue.isNotEmpty()) {
            animationQueue.first().startAnimation()
            animating = true
            sUi.disable()
        }
    }

    fun queueAnimation(animation: Animation) {
        animation.observer = this
        animationQueue.add(animation)
    }

    override fun notify(animation: Animation) {
        animationQueue.remove(animation)
        animating = false
        if (animationQueue.isEmpty()) {
            sUi.enable()
        }
    }

}
