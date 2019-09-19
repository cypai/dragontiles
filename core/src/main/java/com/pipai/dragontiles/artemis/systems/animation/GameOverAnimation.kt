package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.pipai.dragontiles.utils.getLogger

class GameOverAnimation(world: World) : Animation(world) {

    private val logger = getLogger()

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        logger.info("You lost! Game Over.")
        Gdx.app.exit()
        endAnimation()
    }

}
