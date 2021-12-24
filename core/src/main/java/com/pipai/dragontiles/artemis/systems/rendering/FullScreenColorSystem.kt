package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.DepthComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.mapper

class FullScreenColorSystem(game: DragonTilesGame) : BaseSystem() {

    private val config = game.gameConfig
    private val skin = game.skin

    private var bgColor: Color = Color(0f, 0f, 0f, 0f)
    private var targetAlpha = 0.7f

    private var t = 0f
    private var maxT = 0f
    private var state: State = State.NONE

    private var initted = false
    private var fsId: EntityId = 0

    private val mDepth by mapper<DepthComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mXy by mapper<XYComponent>()

    override fun processSystem() {
        if (!initted) {
            initted = true
            fsId = world.create()
            val cSprite = mSprite.create(fsId)
            cSprite.sprite = skin.getSprite("white")
            cSprite.sprite.color = bgColor
            cSprite.width = config.resolution.width.toFloat()
            cSprite.height = config.resolution.height.toFloat()
            mDepth.create(fsId).depth = -1
            mXy.create(fsId)
        }
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
        val cSprite = mSprite.get(fsId)
        cSprite.sprite.color = bgColor
        if (state != State.NONE) {
            t++
            if (t > maxT) {
                state = State.NONE
            }
        }
    }

    fun fadeIn(time: Int, color: Color = Color.BLACK) {
        bgColor.set(color.r, color.b, color.g, 0f)
        t = 0f
        maxT = time.toFloat()
        state = State.FADE_IN
    }

    fun fadeOut(time: Int, color: Color = Color.BLACK) {
        bgColor.set(color.r, color.b, color.g, 1f)
        t = 0f
        maxT = time.toFloat()
        state = State.FADE_OUT
    }

    private enum class State {
        FADE_IN, FADE_OUT, NONE
    }

}
