package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.TimerComponent
import com.pipai.dragontiles.utils.getLogger

abstract class Animation(val duration: Float) {
    constructor() : this(-1f)

    private val logger = getLogger()
    protected lateinit var world: World
    protected lateinit var game: DragonTilesGame
    private var endCalled = false
    private lateinit var observer: AnimationObserver

    fun init(world: World, game: DragonTilesGame) {
        this.world = world
        var injected = false
        var count = 0
        while (!injected && count < 5) {
            try {
                world.inject(this)
                injected = true
            } catch (e: Exception) {
                logger.error("Failed to inject, try again", e)
                Thread.sleep(1)
                count++
            }
        }
        this.game = game
        endCalled = false
    }

    fun initObserver(observer: AnimationObserver) {
        this.observer = observer
    }

    abstract fun startAnimation()

    open fun process() {
    }

    fun endAnimation() {
        endAnimation(1f / 60f)
    }

    fun endAnimation(delay: Float) {
        if (!endCalled) {
            endCalled = true
            val id = world.create()
            val cTimer = world.getMapper(TimerComponent::class.java).create(id)
            cTimer.maxT = if (delay >= 0f) delay else 1f / 60f
            cTimer.onEnd = EndStrategy.DESTROY
            cTimer.onEndCallback = {
                observer.notify(this)
            }
        }
    }

}

interface AnimationObserver {
    fun notify(animation: Animation)
}
