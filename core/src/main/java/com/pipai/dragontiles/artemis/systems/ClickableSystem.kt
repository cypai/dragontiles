package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class ClickableSystem(private val config: GameConfig) : NoProcessingSystem(), InputProcessor {

    private val mClickable by mapper<ClickableComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mRadial by mapper<RadialSpriteComponent>()
    private val mXy by mapper<XYComponent>()

    private val sEvent by system<EventSystem>()

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val mouseX = screenX.toFloat()
        val mouseY = config.resolution.height - screenY.toFloat()
        world.fetch(allOf(ClickableComponent::class, SpriteComponent::class))
                .forEach {
                    val hover = mSprite.get(it).sprite.boundingRectangle.contains(mouseX, mouseY)
                    if (hover) {
                        val cClick = mClickable.get(it)
                        cClick.callback?.invoke()
                        if (cClick.eventGenerator != null) {
                            sEvent.dispatch(cClick.eventGenerator?.invoke(button))
                        }
                    }
                }
        world.fetch(allOf(ClickableComponent::class, ActorComponent::class))
            .forEach {
                val hover = mActor.get(it).actor.boundingRectangle().contains(mouseX, mouseY)
                if (hover) {
                    val cClick = mClickable.get(it)
                    cClick.callback?.invoke()
                    if (cClick.eventGenerator != null) {
                        sEvent.dispatch(cClick.eventGenerator?.invoke(button))
                    }
                }
            }
        world.fetch(allOf(ClickableComponent::class, XYComponent::class, RadialSpriteComponent::class))
                .forEach {
                    val cRadial = mRadial.get(it)
                    val cXy = mXy.get(it)
                    val bounds = CollisionBounds.CollisionBoundingBox(0f, 0f, cRadial.sprite.width(), cRadial.sprite.height())
                    val hover = CollisionUtils.withinBounds(mouseX, mouseY, cXy.x, cXy.y, bounds)
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
