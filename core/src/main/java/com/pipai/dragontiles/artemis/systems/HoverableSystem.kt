package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.HoverableComponent
import com.pipai.dragontiles.artemis.components.RadialSpriteComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class HoverableSystem(private val config: GameConfig) : NoProcessingSystem(), InputProcessor {

    private val mHoverable by mapper<HoverableComponent>()
    private val mSprite by mapper<SpriteComponent>()
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
        world.fetch(allOf(HoverableComponent::class, SpriteComponent::class))
                .forEach {
                    val cHover = mHoverable.get(it)
                    val hover = mSprite.get(it).sprite.boundingRectangle.contains(mouseX, mouseY)
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
        if (!cHover.hovering && hover) {
            cHover.hovering = true
            sEvent.dispatch(cHover.enterEvent)
        }
        if (cHover.hovering && !hover) {
            cHover.hovering = false
            sEvent.dispatch(cHover.exitEvent)
        }

    }

    override fun scrolled(amount: Int) = false
}
