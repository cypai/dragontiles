package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.ActorComponent
import com.pipai.dragontiles.artemis.components.ClickableComponent
import com.pipai.dragontiles.artemis.components.TextLabelComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.PricedItemClickEvent
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class SpellShopUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
) : BaseSystem(), InputProcessor {

    private val logger = getLogger()

    private lateinit var api: GlobalApi

    private val mXy by mapper<XYComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mText by mapper<TextLabelComponent>()
    private val mClickable by mapper<ClickableComponent>()

    private val sEvent by system<EventSystem>()

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        val spellShop = runData.town!!.spellShop
        spellShop.classSpells.forEachIndexed { i, ps ->
            createSpell(ps, SpellCard.cardWidth * 3 + i * SpellCard.cardWidth * 2, SpellCard.cardHeight * 2)
        }
        if (spellShop.colorlessSpell != null) {
            createSpell(spellShop.colorlessSpell!!, SpellCard.cardWidth * 1, SpellCard.cardHeight / 2)
        }
    }

    private fun createSpell(ps: PricedItem, x: Float, y: Float) {
        val entityId = world.create()
        val cActor = mActor.create(entityId)
        val spellCard = SpellCard(game, game.data.getSpell(ps.id), null, game.skin, null)
        cActor.actor = spellCard
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        cText.yOffset = -16f
        cText.color = Color.WHITE
        cText.text = "${ps.price} Gold"
        val cClickable = mClickable.create(entityId)
        cClickable.eventGenerator = { PricedItemClickEvent(entityId, ps) }
    }

    @Subscribe
    fun handleSpellCardClick(ev: PricedItemClickEvent) {
        if (runData.hero.gold >= ev.pricedItem.price) {
            api.gainGoldImmediate(-ev.pricedItem.price)
            val spell = game.data.getSpell(ev.pricedItem.id)
            logger.info("Adding ${spell.id} to deck at price ${ev.pricedItem.price}")
            api.addSpellToDeck(spell)
            world.delete(ev.entityId)
        }
    }

    override fun processSystem() {
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            game.screen = TownScreen(game, runData, false)
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
