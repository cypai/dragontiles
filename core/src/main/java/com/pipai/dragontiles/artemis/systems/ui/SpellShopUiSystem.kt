package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class SpellShopUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData,
) : BaseSystem(), InputProcessor {

    private val logger = getLogger()

    private lateinit var api: GlobalApi
    private val town = runData.town!!

    private val mXy by mapper<XYComponent>()
    private val mText by mapper<TextComponent>()
    private val mPrice by mapper<PriceComponent>()

    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        val spellShop = town.spellShop
        spellShop.classSpells.forEachIndexed { i, ps ->
            if (i < 3) {
                createSpell(ps, (i + 2) * 1.5f * SpellCard.cardWorldWidth, 1f)
            } else {
                createSpell(ps, (i - 1) * 1.5f * SpellCard.cardWorldWidth, 4.5f)
            }
        }
        if (spellShop.colorlessSpell != null) {
            createSpell(spellShop.colorlessSpell!!, 1.5f, 1f)
        }
        sideboardSpace(1.5f, 4.5f)
    }

    private fun sideboardSpace(x: Float, y: Float) {
        val table = Table()
        table.background = game.skin.getDrawable("frameDrawable")
        val available = !town.boughtSideboard
        val text = if (available) {
            "Sideboard Space"
        } else {
            "Sold Out"
        }
        val label = Label(text, game.skin)
        label.setAlignment(Align.center)
        label.wrap = true
        table.add(label)
            .width(SpellCard.cardWidth)
            .expand()
        table.row()
        val screenXy = game.camera.project(Vector3(x, y, 0f))
        table.width = SpellCard.cardWidth
        table.height = SpellCard.cardHeight
        table.x = screenXy.x
        table.y = screenXy.y

        if (available) {
            val price = 3 + runData.sideboardSpaceBought
            val entityId = world.create()

            val cXy = mXy.create(entityId)
            cXy.setXy(x, y)
            val cText = mText.create(entityId)
            if (runData.hero.gold >= price) {
                cText.color = Color.WHITE
            } else {
                cText.color = Color.RED
            }
            cText.text = "$price Gold"
            mPrice.create(entityId).price = price

            table.touchable = Touchable.enabled
            table.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (runData.hero.gold >= price) {
                        table.remove()
                        api.gainGoldImmediate(-price)
                        recalculatePriceColor()
                        runData.hero.sideboardSize++
                        runData.sideboardSpaceBought++
                        if (!town.boughtSpell) {
                            town.actions--
                        }
                        town.boughtSpell = true
                        town.boughtSideboard = true
                        label.setText("Sold Out")

                        world.delete(entityId)
                    }
                }
            })
        }
        stage.addActor(table)
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

    private fun createSpell(ps: PricedItem, x: Float, y: Float) {
        val entityId = world.create()
        mPrice.create(entityId).price = ps.price
        val spell = game.data.getSpell(ps.id)
        val spellCard = SpellCard(game, spell, null, game.skin, null)
        val screenXy = game.camera.project(Vector3(x, y, 0f))
        spellCard.x = screenXy.x
        spellCard.y = screenXy.y
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        if (runData.hero.gold >= ps.price) {
            cText.color = Color.WHITE
        } else {
            cText.color = Color.RED
        }
        cText.text = "${ps.price} Gold"
        spellCard.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                sTooltip.addSpell(spell)
                sTooltip.showTooltip(spellCard.x + 16f + SpellCard.cardWidth)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                sTooltip.hideTooltip()
            }

            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                handleSpellCardClick(entityId, spellCard, ps)
            }
        })
        stage.addActor(spellCard)
    }

    fun handleSpellCardClick(entityId: EntityId, spellCard: SpellCard, ps: PricedItem) {
        if (runData.hero.gold >= ps.price) {
            if (ps == town.spellShop.colorlessSpell) {
                town.spellShop.colorlessSpell = null
            } else {
                town.spellShop.classSpells.remove(ps)
            }
            api.gainGoldImmediate(-ps.price)
            recalculatePriceColor()
            val spell = game.data.getSpell(ps.id)
            logger.info("Adding ${spell.id} to deck at price ${ps.price}")
            api.addSpellToDeck(spell)
            world.delete(entityId)
            spellCard.remove()
            if (!town.boughtSpell) {
                town.actions--
            }
            town.boughtSpell = true
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
