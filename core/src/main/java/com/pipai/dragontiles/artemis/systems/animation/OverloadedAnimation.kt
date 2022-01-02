package com.pipai.dragontiles.artemis.systems.animation

import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem

class OverloadedAnimation : Animation() {

    private lateinit var sFs: FullScreenColorSystem

    override fun startAnimation() {
        sFs.fadeOut(0.5f, Color.WHITE)
        endAnimation()
    }

}
