package com.pipai.dragontiles.artemis.systems.animation

import com.badlogic.gdx.Gdx
import com.pipai.dragontiles.utils.getLogger

class BattleWinAnimation : Animation() {

    private val logger = getLogger()

    override fun startAnimation() {
        logger.info("You win! Game Over.")
        Gdx.app.exit()
        endAnimation()
    }

}
