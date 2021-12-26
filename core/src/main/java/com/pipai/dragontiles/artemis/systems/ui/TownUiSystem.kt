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
import com.pipai.dragontiles.artemis.screens.ItemShopScreen
import com.pipai.dragontiles.artemis.screens.ScribeShopScreen
import com.pipai.dragontiles.artemis.screens.SpellShopScreen
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class TownUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData
) : BaseSystem(), InputProcessor {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mHoverable by mapper<HoverableComponent>()
    private val mText by mapper<TextLabelComponent>()

    private val sTooltip by system<TooltipSystem>()
    private val sEvent by system<EventSystem>()
    private val sMap by system<MapUiSystem>()

    private val ids: MutableList<EntityId> = mutableListOf()

    override fun initialize() {
        recreateTown()
        sMap.canAdvanceMap = true
    }

    private fun recreateTown() {
        ids.forEach {
            world.delete(it)
        }
        ids.clear()
        val town = runData.town!!
        // Draw actions left
        val actionId = world.create()
        mXy.create(actionId).setXy(80f, 600f)
        val cActionText = mText.create(actionId)
        cActionText.text = "Actions: ${town.actions}"

        val innId = world.create()
        mXy.create(innId).setXy(80f, 20f)
        val innSprite = mSprite.create(innId)
        innSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/inn.png", Texture::class.java))
        innSprite.width = 120f
        innSprite.height = 120f
        val innHover = mHoverable.create(innId)
        innHover.enterCallback = {
            sTooltip.addText("Inn", "Heal 25% of your Max HP for 2 Gold.", false)
            sTooltip.showTooltip()
        }
        innHover.exitCallback = {
            sTooltip.hideTooltip()
        }
        if (town.actions > 0 && runData.hero.gold > 1) {
            mClickable.create(innId).callback = {
                town.actions--
                val api = GlobalApi(game.data, runData, sEvent)
                api.gainGoldImmediate(-2)
                api.gainHpImmediate((runData.hero.hpMax * 0.25f).toInt())
                recreateTown()
            }
        } else {
            innSprite.sprite.color = Color.GRAY
        }

        val spellShopId = world.create()
        mXy.create(spellShopId).setXy(80f, 200f)
        val spellShopSprite = mSprite.create(spellShopId)
        spellShopSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/spell_shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtSpell || town.boughtSideboard) {
            mClickable.create(spellShopId).callback = {
                game.screen = SpellShopScreen(game, runData)
            }
            val spellShopHover = mHoverable.create(spellShopId)
            spellShopHover.enterCallback = {
                sTooltip.addText(
                    "Spell Shop",
                    "Browsing is free, but buying takes 1 action. After buying, you may buy more as a free action from the same shop.",
                    false
                )
                sTooltip.showTooltip()
            }
            spellShopHover.exitCallback = {
                sTooltip.hideTooltip()
            }
        } else {
            spellShopSprite.sprite.color = Color.GRAY
        }

        val itemShopId = world.create()
        mXy.create(itemShopId).setXy(400f, 200f)
        val itemShopSprite = mSprite.create(itemShopId)
        itemShopSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtItem) {
            mClickable.create(itemShopId).callback = {
                game.screen = ItemShopScreen(game, runData)
            }
            val itemShopHover = mHoverable.create(itemShopId)
            itemShopHover.enterCallback = {
                sTooltip.addText(
                    "Item Shop",
                    "Browsing is free, but buying takes 1 action. After buying, you may buy more as a free action from the same shop.",
                    false
                )
                sTooltip.showTooltip()
            }
            itemShopHover.exitCallback = {
                sTooltip.hideTooltip()
            }
        } else {
            itemShopSprite.sprite.color = Color.GRAY
        }

        val scribeId = world.create()
        mXy.create(scribeId).setXy(720f, 200f)
        val scribeSprite = mSprite.create(scribeId)
        scribeSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/upgrade_shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtUpgrade) {
            mClickable.create(scribeId).callback = {
                game.screen = ScribeShopScreen(game, runData)
            }
            val scribeHover = mHoverable.create(scribeId)
            scribeHover.enterCallback = {
                sTooltip.addText(
                    "Upgrades Shop",
                    "Browsing is free, but buying takes 1 action. After buying, you may buy more as a free action from the same shop",
                    false
                )
                sTooltip.showTooltip()
            }
            scribeHover.exitCallback = {
                sTooltip.hideTooltip()
            }
        } else {
            scribeSprite.sprite.color = Color.GRAY
        }

        if (town.actions > 0 && !town.solicited) {
            val solicitId = world.create()
            mXy.create(solicitId).setXy(480f, 20f)
            val solicitSprite = mSprite.create(solicitId)
            solicitSprite.sprite =
                Sprite(game.assets.get("assets/binassets/graphics/textures/solicit.png", Texture::class.java))
            val solicitClick = mClickable.create(solicitId)
            solicitClick.callback = {
                town.actions--
                town.solicited = true
                GlobalApi(game.data, runData, sEvent).gainGoldImmediate(1)
                recreateTown()
            }
            val solicitHover = mHoverable.create(solicitId)
            solicitHover.enterCallback = {
                sTooltip.addText("Solicit", "Beg for 1 Gold.", false)
                sTooltip.showTooltip()
            }
            solicitHover.exitCallback = {
                sTooltip.hideTooltip()
            }
            ids.add(solicitId)
        }

        ids.addAll(listOf(actionId, innId, scribeId, spellShopId, itemShopId))
    }

    override fun processSystem() {
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.M) {
            if (sMap.showing) {
                ids.forEach {
                    world.delete(it)
                }
                ids.clear()
            } else {
                recreateTown()
            }
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
