package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class HoverableSystem(private val game: DragonTilesGame) : NoProcessingSystem(), InputProcessor {

    private val mHoverable by mapper<HoverableComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mSpine by mapper<SpineComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mXy by mapper<XYComponent>()

    private val sEvent by system<EventSystem>()

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val mouseXy = game.viewport.unproject(Vector2(screenX.toFloat(), screenY.toFloat()))
        world.scry(allOf(XYComponent::class, HoverableComponent::class, SpriteComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cHover = mHoverable.get(it)
                val cSprite = mSprite.get(it)
                val hover = Rectangle(cXy.x, cXy.y, cSprite.width, cSprite.height).contains(mouseXy)
                updateHover(cHover, hover)
            }
        world.scry(allOf(XYComponent::class, HoverableComponent::class, SpineComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cHover = mHoverable.get(it)
                val cSpine = mSpine.get(it)
                val width = cSpine.skeleton.actualWidth()
                val height = cSpine.skeleton.actualHeight()
                val hover = Rectangle(cXy.x - width / 2f, cXy.y, width, height).contains(mouseXy)
                updateHover(cHover, hover)
            }
        world.scry(allOf(HoverableComponent::class, ActorComponent::class))
            .forEach {
                val cHover = mHoverable.get(it)
                val hover = mActor.get(it).actor.boundingRectangle().contains(mouseXy)
                updateHover(cHover, hover)
            }

        return false
    }

    private fun updateHover(cHover: HoverableComponent, hover: Boolean) {
        if ((!cHover.hovering || cHover.recheck) && hover) {
            if (cHover.hovering) {
                cHover.recheck = false
            }
            cHover.hovering = true
            cHover.enterEvent?.let { sEvent.dispatch(it) }
            cHover.enterCallback?.invoke()
        }
        if (cHover.hovering && !hover) {
            cHover.hovering = false
            cHover.exitEvent?.let { sEvent.dispatch(it) }
            cHover.exitCallback?.invoke()
        }

    }

    override fun scrolled(amountX: Float, amountY: Float) = false
}
