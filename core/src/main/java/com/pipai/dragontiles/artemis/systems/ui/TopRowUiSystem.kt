package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.GoldChangeEvent
import com.pipai.dragontiles.artemis.events.PricedSpellClickEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.dungeon.RunData
import net.mostlyoriginal.api.event.common.Subscribe
import java.lang.Integer.min

class TopRowUiSystem(
    game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage
) : NoProcessingSystem() {

    private val skin = game.skin
    private val config = game.gameConfig

    private val rootTable = Table()
    private val topRow = Table()
    private var hp = runData.hero.hp
    private var hpMax = runData.hero.hpMax
    private var flux = runData.hero.flux
    private var fluxMax = runData.hero.fluxMax
    private var gold = runData.hero.gold
    private val hpLabel = Label("HP: $hp/$hpMax", skin)
    private val fluxLabel = Label("Flux: $flux/$fluxMax", skin)
    private val goldLabel = Label("Gold: $gold", skin)

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

    @Subscribe
    fun handleGoldChange(ev: GoldChangeEvent) {
        goldLabel.setText("Gold: ${runData.hero.gold}")
    }
}
