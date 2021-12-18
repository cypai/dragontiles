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
import com.pipai.dragontiles.artemis.events.PricedSpellClickEvent
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.data.PricedSpell
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
        api = GlobalApi(runData, sEvent)
        val spellShop = runData.town!!.spellShop
        if (spellShop.cantrip != null) {
            createSpell(spellShop.cantrip!!, SpellCard.cardWidth * 1, SpellCard.cardHeight * 2)
        }
        spellShop.classSpells.forEachIndexed { i, ps ->
            createSpell(ps, SpellCard.cardWidth * 3 + i * SpellCard.cardWidth * 2, SpellCard.cardHeight * 2)
        }
        if (spellShop.colorlessSpell != null) {
            createSpell(spellShop.colorlessSpell!!, SpellCard.cardWidth * 1, SpellCard.cardHeight / 2)
        }
    }

    private fun createSpell(ps: PricedSpell, x: Float, y: Float) {
        val entityId = world.create()
        val cActor = mActor.create(entityId)
        val spellCard = SpellCard(game, ps.spell, null, game.skin, null)
        cActor.actor = spellCard
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cText = mText.create(entityId)
        cText.yOffset = -16f
        cText.color = Color.WHITE
        cText.text = "${ps.price} Gold"
        val cClickable = mClickable.create(entityId)
        cClickable.eventGenerator = { PricedSpellClickEvent(ps) }
    }

    @Subscribe
    fun handleSpellCardClick(ev: PricedSpellClickEvent) {
        if (runData.hero.gold >= ev.pricedSpell.price) {
            runData.hero.gold -= ev.pricedSpell.price
            logger.info("Adding ${ev.pricedSpell.spell.id} to deck at price ${ev.pricedSpell.price}")
            api.addSpellToDeck(ev.pricedSpell.spell)
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
