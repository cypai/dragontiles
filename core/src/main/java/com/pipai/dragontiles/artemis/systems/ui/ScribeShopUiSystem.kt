package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.PricedItemClickEvent
import com.pipai.dragontiles.artemis.events.UpgradeSpellQueryEvent
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class ScribeShopUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
) : BaseSystem(), InputProcessor {

    private lateinit var api: GlobalApi
    private val town = runData.town!!

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mText by mapper<TextComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mHoverableComponent by mapper<HoverableComponent>()
    private val mPrice by mapper<PriceComponent>()

    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        val scribeShop = town.scribe
        scribeShop.upgrades.forEachIndexed { i, ps ->
            if (i < 4) {
                createUpgrade(ps, 3f + i * 1.5f * SpellCard.cardWorldWidth, 2f)
            } else {
                createUpgrade(ps, 3f + (i - 4) * 1.5f * SpellCard.cardWorldWidth, 4.5f)
            }
        }
    }

    private fun createUpgrade(ps: PricedItem, x: Float, y: Float) {
        val entityId = world.create()
        mPrice.create(entityId).price = ps.price
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(game.assets.get(upgradeAssetPath(game.data.getSpellUpgrade(ps.id).assetName), Texture::class.java))
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        if (runData.hero.gold >= ps.price) {
            cText.color = Color.WHITE
        } else {
            cText.color = Color.RED
        }
        cText.text = "${ps.price} Gold"
        val cClickable = mClickable.create(entityId)
        cClickable.eventGenerator = { PricedItemClickEvent(entityId, ps) }
        val cHover = mHoverableComponent.create(entityId)
        cHover.enterCallback = {
            sTooltip.addLocalized(game.data.getSpellUpgrade(ps.id))
            sTooltip.showTooltip()
        }
        cHover.exitCallback = {
            sTooltip.hideTooltip()
        }
    }

    @Subscribe
    fun handleClick(ev: PricedItemClickEvent) {
        if (runData.hero.gold >= ev.pricedItem.price) {
            town.scribe.upgrades.remove(ev.pricedItem)
            api.gainGoldImmediate(-ev.pricedItem.price)
            recalculatePriceColor()
            sEvent.dispatch(UpgradeSpellQueryEvent(game.data.getSpellUpgrade(ev.pricedItem.id)))
            world.delete(ev.entityId)
            if (!town.boughtUpgrade) {
                town.actions--
            }
            town.boughtUpgrade = true
        }
    }

    private fun recalculatePriceColor() {
        world.fetch(allOf(PriceComponent::class, TextComponent::class))
            .forEach {
                val cPrice = mPrice.get(it)
                if (cPrice.price > runData.hero.gold) {
                    mText.get(it).color = Color.RED
                }
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
