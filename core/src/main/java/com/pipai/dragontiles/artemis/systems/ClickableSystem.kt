package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.InputProcessor
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.artemis.components.ClickableComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class ClickableSystem(private val config: GameConfig) : NoProcessingSystem(), InputProcessor {

    private val mClickable by mapper<ClickableComponent>()
    private val mSprite by mapper<SpriteComponent>()

    private val sEvent by system<EventSystem>()

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        world.fetch(allOf(ClickableComponent::class, SpriteComponent::class))
                .filter { mSprite.get(it).sprite.boundingRectangle.contains(screenX.toFloat(), config.resolution.height - screenY.toFloat()) }
                .forEach { sEvent.dispatch(mClickable.get(it).event) }

        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amount: Int) = false
}
