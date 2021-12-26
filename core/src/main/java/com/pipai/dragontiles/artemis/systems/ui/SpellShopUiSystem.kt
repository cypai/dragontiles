package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.PricedItemClickEvent
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class SpellShopUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData,
) : BaseSystem(), InputProcessor {

    private val logger = getLogger()

    private lateinit var api: GlobalApi
    private val town = runData.town!!

    private val mXy by mapper<XYComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mText by mapper<TextLabelComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mPrice by mapper<PriceComponent>()

    private val sEvent by system<EventSystem>()

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        val spellShop = town.spellShop
        spellShop.classSpells.forEachIndexed { i, ps ->
            if (i < 3) {
                createSpell(ps, SpellCard.cardWidth * 3 + i * SpellCard.cardWidth * 2, SpellCard.cardHeight * 2)
            } else {
                createSpell(ps, SpellCard.cardWidth * 3 + (i - 3) * SpellCard.cardWidth * 2, SpellCard.cardHeight / 2)
            }
        }
        if (spellShop.colorlessSpell != null) {
            createSpell(spellShop.colorlessSpell!!, SpellCard.cardWidth, SpellCard.cardHeight / 2)
        }
        sideboardSpace(SpellCard.cardWidth, SpellCard.cardHeight * 2)
    }

    private fun sideboardSpace(x: Float, y: Float) {
        val table = Table()
        table.background  = game.skin.getDrawable("frameDrawable")
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
            .expand()
        table.row()
        table.width = SpellCard.cardWidth
        table.height = SpellCard.cardHeight
        table.x = x
        table.y = y

        if (available) {
            val price = 3 + runData.sideboardSpaceBought
            val entityId = world.create()

            val cXy = mXy.create(entityId)
            cXy.setXy(x, y)
            val cText = mText.create(entityId)
            cText.yOffset = -16f
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
                        town.boughtSpell = true
                        town.boughtSideboard = true
                        if (!town.boughtSpell) {
                            town.actions--
                        }
                        label.setText("Sold Out")

                        world.delete(entityId)
                    }
                }
            })
        }
        stage.addActor(table)
    }

    private fun recalculatePriceColor() {
        world.fetch(allOf(PriceComponent::class, TextLabelComponent::class))
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
        val cActor = mActor.create(entityId)
        val spellCard = SpellCard(game, game.data.getSpell(ps.id), null, game.skin, null)
        cActor.actor = spellCard
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
        cClickable.eventGenerator = { PricedItemClickEvent(entityId, ps) }
    }

    @Subscribe
    fun handleSpellCardClick(ev: PricedItemClickEvent) {
        if (runData.hero.gold >= ev.pricedItem.price) {
            api.gainGoldImmediate(-ev.pricedItem.price)
            recalculatePriceColor()
            val spell = game.data.getSpell(ev.pricedItem.id)
            logger.info("Adding ${spell.id} to deck at price ${ev.pricedItem.price}")
            api.addSpellToDeck(spell)
            world.delete(ev.entityId)
            town.boughtSpell = true
            if (!town.boughtSpell) {
                town.actions--
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
