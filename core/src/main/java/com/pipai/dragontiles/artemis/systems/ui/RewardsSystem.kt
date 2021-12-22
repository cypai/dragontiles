package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.combat.CombatRewards
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class RewardsSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
    private val rewards: CombatRewards,
) : NoProcessingSystem() {

    private val skin = game.skin

    private val rewardsTable = Table()
    private val rewardsTitle = Label("Rewards! Choose a spell.", skin, "white")
    private val skipBtn = TextButton("  Skip (+1 gold)  ", skin)

    private val spellRewards: List<Spell> = game.heroSpells.getRandomClassSpells(runData, 3)

    private val sCombat by system<CombatControllerSystem>()
    private val sMap by system<MapUiSystem>()
    private val sFsc by system<FullScreenColorSystem>()
    private val sEvent by system<EventSystem>()

    private lateinit var api: GlobalApi

    override fun initialize() {
        api = GlobalApi(runData, sEvent)

        skipBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                runData.hero.gold += 1
                sMap.showMap()
                rewardsTable.remove()
            }
        })

        rewardsTable.setFillParent(true)
        rewardsTable.add(rewardsTitle)
            .colspan(3)
            .top()
            .padTop(64f)
            .center()
        rewardsTable.row()
        spellRewards.forEach { spell ->
            val spellCard = SpellCard(game, spell, null, game.skin, sCombat.controller.api)
            spellCard.addClickCallback { _, _ ->
                api.addSpellToDeck(spell)
                sMap.showMap()
                rewardsTable.remove()
            }
            rewardsTable.add(spellCard)
        }
        rewardsTable.row()
        rewardsTable.add()
        rewardsTable.add(skipBtn)
        rewardsTable.add()
        rewardsTable.row()
    }

    fun revealRewards() {
        api.gainGoldImmediate(rewards.gold)
        if (rewards.randomRelic) {
            api.gainRelicImmediate(runData.relicData.availableRelics.choose(runData.rng))
        }
        if (rewards.relic != null) {
            api.gainRelicImmediate(rewards.relic)
        }
        sFsc.fadeIn(10)
        stage.addActor(rewardsTable)
    }
}
