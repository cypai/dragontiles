package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.TimerComponent

abstract class Animation {
    protected lateinit var world: World
    protected lateinit var game: DragonTilesGame
    private lateinit var observer: AnimationObserver

    fun init(world: World, game: DragonTilesGame) {
        this.world = world
        world.inject(this)
        this.game = game
    }

    fun initObserver(observer: AnimationObserver) {
        this.observer = observer
    }

    abstract fun startAnimation()

    fun endAnimation() {
        endAnimation(1f / 60f)
    }

    fun endAnimation(delay: Float) {
        val id = world.create()
        val cTimer = world.getMapper(TimerComponent::class.java).create(id)
        cTimer.maxT = if (delay >= 0f) delay else 1f / 60f
        cTimer.onEnd = EndStrategy.DESTROY
        cTimer.onEndCallback = {
            observer.notify(this)
        }
    }

}

interface AnimationObserver {
    fun notify(animation: Animation)
}
