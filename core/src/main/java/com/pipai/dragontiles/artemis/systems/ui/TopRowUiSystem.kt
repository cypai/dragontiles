package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.dungeon.RunData

class TopRowUiSystem(game: DragonTilesGame,
                     runData: RunData,
                     private val stage: Stage) : NoProcessingSystem() {

    private val skin = game.skin
    private val config = game.gameConfig

    private val rootTable = Table()
    private val topRow = Table()
    private val hpLabel = Label("${runData.hero.hp}/${runData.hero.hpMax}", skin)

    override fun initialize() {
        rootTable.setFillParent(true)
        topRow.background = skin.getDrawable("frameDrawable")
        topRow.add(Label("Elementalist", skin))
                .width(260f)
                .pad(8f)
                .padLeft(16f)
                .left()
                .top()
        topRow.add(hpLabel)
                .width(120f)
        topRow.add()
                .expand()

        rootTable.add(topRow)
                .width(config.resolution.width.toFloat())
                .top()
                .left()
        rootTable.row()
        rootTable.add()
                .expand()
        stage.addActor(rootTable)
    }

    fun setHpRelative(amount: Int) {
        val tokens = hpLabel.text.split("/")
        val hp = tokens[0].toInt()
        val hpMax = tokens[1].toInt()
        setHp(hp + amount, hpMax)
    }

    fun setHp(hp: Int, hpMax: Int) {
        hpLabel.setText("$hp/$hpMax")
    }

}
