package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.EventScreen
import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.dungeon.DungeonInitializer
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.dungeon.RunHistory
import com.pipai.dragontiles.dungeonevents.PlainsStartEvent
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.relics.RelicData
import java.util.*

class MainMenuUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage
) : BaseSystem() {

    private val skin = game.skin

    private val rootTable = Table()

    private val newGameLabel = Label("New Game", skin)
    private val tutorialLabel = Label("Tutorial", skin)
    private val quitLabel = Label("Quit", skin)

    override fun initialize() {
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")
        rootTable.add(Label("Dragontiles", skin))
        rootTable.row()
        rootTable.add(newGameLabel)
            .padTop(64f)
        rootTable.row()
        rootTable.add(tutorialLabel)
        rootTable.row()
        rootTable.add(quitLabel)
        rootTable.row()

        newGameLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (game.save.requireTutorial) {
                    game.save.requireTutorial = false
                    // TODO: start tutorial
                }
                val hero = Elementalist().generateHero("Elementalist")

                val runData = RunData(
                    DungeonInitializer(),
                    hero,
                    RelicData(GameData.relics.toMutableList()),
                    null,
                    0,
                    GameData.BASE_POTION_CHANCE,
                    RunHistory(mutableListOf()),
                    Random(),
                )
                runData.dungeonMap.generateMap(runData.rng)
                game.save.currentRun = runData
                game.writeSave()
                game.screen = EventScreen(game, runData, PlainsStartEvent())
            }
        })
        tutorialLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                // TODO: start tutorial
            }
        })
        quitLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        stage.addActor(rootTable)
    }

    override fun processSystem() {
    }

}
