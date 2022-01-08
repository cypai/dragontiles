package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.FloatArray
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class ClickableSystem(private val game: DragonTilesGame) : NoProcessingSystem(), InputProcessor {

    private val mClickable by mapper<ClickableComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mSpine by mapper<SpineComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mXy by mapper<XYComponent>()

    private val sEvent by system<EventSystem>()

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val mouseXy = game.viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        world.fetch(allOf(XYComponent::class, ClickableComponent::class, SpriteComponent::class))
            .forEach {
                val cXy  = mXy.get(it)
                val cSprite = mSprite.get(it)
                val hover = Rectangle(cXy.x, cXy.y, cSprite.width, cSprite.height).contains(mouseXy)
                if (hover) {
                    val cClick = mClickable.get(it)
                    cClick.callback?.invoke()
                    if (cClick.eventGenerator != null) {
                        sEvent.dispatch(cClick.eventGenerator?.invoke(button))
                    }
                }
            }
        world.fetch(allOf(ClickableComponent::class, SpineComponent::class))
            .forEach {
                val cSpine = mSpine.get(it)
                if (cSpine.skeleton.boundingRectangle().contains(mouseXy)) {
                    val cClick = mClickable.get(it)
                    cClick.callback?.invoke()
                    if (cClick.eventGenerator != null) {
                        sEvent.dispatch(cClick.eventGenerator?.invoke(button))
                    }
                }
            }
        world.fetch(allOf(ClickableComponent::class, ActorComponent::class))
            .forEach {
                val hover = mActor.get(it).actor.boundingRectangle().contains(mouseXy)
                if (hover) {
                    val cClick = mClickable.get(it)
                    cClick.callback?.invoke()
                    if (cClick.eventGenerator != null) {
                        sEvent.dispatch(cClick.eventGenerator?.invoke(button))
                    }
                }
            }
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
