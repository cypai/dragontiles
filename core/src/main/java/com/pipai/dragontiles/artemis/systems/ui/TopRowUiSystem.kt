package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.dungeon.RunData
import java.lang.Integer.min

class TopRowUiSystem(
    game: DragonTilesGame,
    runData: RunData,
    private val stage: Stage
) : NoProcessingSystem() {

    private val skin = game.skin
    private val config = game.gameConfig

    private val rootTable = Table()
    private val topRow = Table()
    private var hp = 0
    private var hpMax = 0
    private var flux = 0
    private var fluxMax = 0
    private val hpLabel = Label("HP: ${runData.hero.hp}/${runData.hero.hpMax}", skin)
    private val fluxLabel = Label("Flux: ${runData.hero.hp}/${runData.hero.hpMax}", skin)
    private val goldLabel = Label("Gold: ${runData.hero.gold}", skin)

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
            .width(160f)
        topRow.add(fluxLabel)
            .width(160f)
        topRow.add(goldLabel)
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
        setHp(hp + amount, hpMax)
    }

    fun setHp(hp: Int, hpMax: Int) {
        this.hp = hp
        this.hpMax = hpMax
        hpLabel.setText("HP: $hp/$hpMax")
    }

    fun setFluxRelative(amount: Int) {
        setFlux(min(flux + amount, fluxMax), fluxMax)
    }

    fun setFlux(flux: Int, fluxMax: Int) {
        this.flux = flux
        this.fluxMax = fluxMax
        fluxLabel.setText("Flux: $flux/$fluxMax")
    }
}
