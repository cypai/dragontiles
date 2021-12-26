package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.potionAssetPath
import com.pipai.dragontiles.utils.relicAssetPath
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class ItemShopUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
) : BaseSystem(), InputProcessor {

    private lateinit var api: GlobalApi
    private val town = runData.town!!

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mText by mapper<TextLabelComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mHoverableComponent by mapper<HoverableComponent>()

    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        val itemShop = town.itemShop
        itemShop.relics.forEachIndexed { i, ps ->
            createRelic(ps, SpellCard.cardWidth * 2 + i * SpellCard.cardWidth * 2, SpellCard.cardHeight * 2)
        }
        itemShop.potions.forEachIndexed { i, ps ->
            createPotion(ps, SpellCard.cardWidth * 2 + i * SpellCard.cardWidth * 2, SpellCard.cardHeight / 2)
        }
    }

    private fun createRelic(ps: PricedItem, x: Float, y: Float) {
        val entityId = world.create()
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(game.assets.get(relicAssetPath(game.data.getRelic(ps.id).assetName), Texture::class.java))
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        cText.yOffset = -16f
        if (runData.hero.gold >= ps.price) {
            cText.color = Color.WHITE
        } else {
            cText.color = Color.RED
        }
        cText.text = "${ps.price} Gold"
        val cClickable = mClickable.create(entityId)
        cClickable.callback = {
            handleClickRelic(entityId, ps)
        }
        val cHover = mHoverableComponent.create(entityId)
        cHover.enterCallback = {
            sTooltip.addLocalized(game.data.getRelic(ps.id))
            sTooltip.showTooltip()
        }
        cHover.exitCallback = {
            sTooltip.hideTooltip()
        }
    }

    private fun createPotion(ps: PricedItem, x: Float, y: Float) {
        val entityId = world.create()
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(game.assets.get(potionAssetPath(game.data.getPotion(ps.id).assetName), Texture::class.java))
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        cText.yOffset = -16f
        if (runData.hero.gold >= ps.price) {
            cText.color = Color.WHITE
        } else {
            cText.color = Color.RED
        }
        cText.text = "${ps.price} Gold"
        val cClickable = mClickable.create(entityId)
        cClickable.callback = {
            handleClickPotion(entityId, ps)
        }
        val cHover = mHoverableComponent.create(entityId)
        cHover.enterCallback = {
            sTooltip.addLocalized(game.data.getPotion(ps.id))
            sTooltip.showTooltip()
        }
        cHover.exitCallback = {
            sTooltip.hideTooltip()
        }
    }

    private fun handleClickRelic(entityId: EntityId, ps: PricedItem) {
        if (runData.hero.gold >= ps.price) {
            api.gainGoldImmediate(-ps.price)
            api.gainRelicImmediate(game.data.getRelic(ps.id))
            world.delete(entityId)
            town.boughtItem = true
            handleBuy()
        }
    }

    private fun handleClickPotion(entityId: EntityId, ps: PricedItem) {
        if (runData.hero.gold >= ps.price) {
            api.gainGoldImmediate(-ps.price)
            api.gainPotion(game.data.getPotion(ps.id))
            world.delete(entityId)
            town.boughtItem = true
            handleBuy()
        }
    }

    private fun handleBuy() {
        if (!town.boughtItem) {
            town.actions--
        }
    }

    override fun processSystem() {
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            game.screen = TownScreen(game, runData)
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
