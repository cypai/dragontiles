package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.DragonTilesGame

class FullScreenColorRenderingSystem(game: DragonTilesGame) : BaseSystem() {

    private val batch = game.spriteBatch
    private val config = game.gameConfig
    private val skin = game.skin

    private var texture: Texture = skin.get("white", Texture::class.java)
    private var bgColor: Color = Color(0f, 0f, 0f, 0f)
    private var targetAlpha = 0.5f

    private var t = 0f
    private var maxT = 0f
    private var state: State = State.NONE

    override fun processSystem() {
        when (state) {
            State.FADE_IN -> {
                bgColor.a = Interpolation.linear.apply(0f, targetAlpha, t / maxT)
            }
            State.FADE_OUT -> {
                bgColor.a = Interpolation.linear.apply(targetAlpha, 0f, t / maxT)
            }
            State.NONE -> {
                // Do nothing
            }
        }
        if (state != State.NONE) {
            t++
            if (t > maxT) {
                state = State.NONE
            }
        }

        batch.color = Color.WHITE
        batch.begin()
        if (bgColor.a > 0) {
            skin.newDrawable("white", bgColor)
                    .draw(batch, 0f, 0f, config.resolution.width.toFloat(), config.resolution.height.toFloat())
        }
        batch.end()
    }

    fun changeTexture(textureName: String) {
        texture = skin.get(textureName, Texture::class.java)
    }

    fun fadeIn(time: Int) {
        t = 0f
        maxT = time.toFloat()
        state = State.FADE_IN
    }

    fun fadeOut(time: Int) {
        t = 0f
        maxT = time.toFloat()
        state = State.FADE_OUT
    }

    private enum class State {
        FADE_IN, FADE_OUT, NONE
    }

}
