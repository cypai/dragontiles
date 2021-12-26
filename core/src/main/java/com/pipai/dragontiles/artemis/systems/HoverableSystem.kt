package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Rectangle
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class HoverableSystem(private val config: GameConfig) : NoProcessingSystem(), InputProcessor {

    private val mHoverable by mapper<HoverableComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mRadial by mapper<RadialSpriteComponent>()
    private val mXy by mapper<XYComponent>()

    private val sEvent by system<EventSystem>()

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val mouseX = screenX.toFloat()
        val mouseY = config.resolution.height - screenY.toFloat()
        world.fetch(allOf(XYComponent::class, HoverableComponent::class, SpriteComponent::class))
                .forEach {
                    val cHover = mHoverable.get(it)
                    val cSprite = mSprite.get(it)
                    val hover = if (cSprite.width == 0f) {
                        cSprite.sprite.boundingRectangle.contains(mouseX, mouseY)
                    } else {
                        val cXy = mXy.get(it)
                        Rectangle(cXy.x, cXy.y, cSprite.width, cSprite.height).contains(mouseX, mouseY)
                    }
                    updateHover(cHover, hover)
                }
        world.fetch(allOf(HoverableComponent::class, ActorComponent::class))
            .forEach {
                val cHover = mHoverable.get(it)
                val hover = mActor.get(it).actor.boundingRectangle().contains(mouseX, mouseY)
                updateHover(cHover, hover)
            }
        world.fetch(allOf(HoverableComponent::class, XYComponent::class, RadialSpriteComponent::class))
                .forEach {
                    val cHover = mHoverable.get(it)
                    val cRadial = mRadial.get(it)
                    val cXy = mXy.get(it)
                    val bounds = CollisionBounds.CollisionBoundingBox(0f, 0f, cRadial.sprite.width(), cRadial.sprite.height())
                    val hover = CollisionUtils.withinBounds(mouseX, mouseY, cXy.x, cXy.y, bounds)
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
