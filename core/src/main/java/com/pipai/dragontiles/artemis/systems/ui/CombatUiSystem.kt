package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.gui.SpellCard

class CombatUiSystem(private val game: DragonTilesGame,
                     private val stage: Stage) : NoProcessingSystem() {

    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val rootTable = Table()
    private val mainTable = Table()
    private val topRow = Table()
    private val spellRow = Table()

    private val hpLabel = Label("80/80", skin)
    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()

    init {
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        val leftSide = 2 * config.resolution.handSideBuffer + config.resolution.handMaxSize
        rootTable.add()
                .width(leftSide)
                .height(config.resolution.height.toFloat())
        rootTable.add(mainTable)
                .width(config.resolution.width - leftSide)
                .height(config.resolution.height.toFloat())
        mainTable.background(game.skin.getDrawable("frameDrawable"))

        topRow.add(Label("Elementalist", skin))
                .width(160f)
        topRow.add(hpLabel)
        mainTable.add(topRow)
                .left()
                .padBottom(16f)
        mainTable.row()

        for (i in 1..9) {
            addSpellCard(i)
            if (i % 3 == 0) {
                spellRow.row()
            }
        }
        mainTable.add(spellRow)
        mainTable.row()
    }

    private fun addSpellCard(number: Int) {
        val spellCard = SpellCard(null, number, game.skin, tileSkin)
        spellRow.add(spellCard)
                .minWidth(spellCard.width)
                .minHeight(spellCard.height)
        spells[number] = spellCard
    }

}
