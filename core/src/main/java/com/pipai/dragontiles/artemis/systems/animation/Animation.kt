package com.pipai.dragontiles.artemis.systems.animation

abstract class Animation {

    lateinit var observer: AnimationObserver

    abstract fun startAnimation()

}

interface AnimationObserver {
    fun notify(animation: Animation)
}
