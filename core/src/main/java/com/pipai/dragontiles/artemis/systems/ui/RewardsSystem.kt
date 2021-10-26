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
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.system

class RewardsSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage
) : NoProcessingSystem() {

    private val skin = game.skin
    private val config = game.gameConfig

    private val rewardsTable = Table()
    private val rewardsTitle = Label("Rewards! Choose a spell.", skin, "white")
    private val skipBtn = TextButton("  Skip (+1 gold)  ", skin)

    private val spellRewards: List<Spell> = game.heroSpells.generateRewards(runData, 3)

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()
    private val sMap by system<MapUiSystem>()
    private val sFsc by system<FullScreenColorSystem>()

    override fun initialize() {
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
            val spellCard = SpellCard(game, spell, null, game.skin, sCombat.controller.api, sTooltip)
            spellCard.addClickCallback { _, _ ->
                if (runData.hero.spells.size >= runData.hero.spellsSize) {
                    runData.hero.sideDeck.add(spell)
                } else {
                    runData.hero.spells.add(spell)
                }
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
        sFsc.fadeIn(10)
        stage.addActor(rewardsTable)
    }
}
