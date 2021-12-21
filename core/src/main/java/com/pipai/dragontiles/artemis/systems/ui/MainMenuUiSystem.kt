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
import com.pipai.dragontiles.dungeon.PlainsDungeon
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.dungeonevents.PlainsStartEvent
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.relics.RelicData
import com.pipai.dragontiles.relics.Transmuter
import com.pipai.dragontiles.spells.common.Blast
import com.pipai.dragontiles.spells.common.Bump
import com.pipai.dragontiles.spells.common.Fetch
import com.pipai.dragontiles.spells.elementalist.BurnRune
import com.pipai.dragontiles.spells.elementalist.Fireball
import com.pipai.dragontiles.spells.elementalist.PiercingStrike
import com.pipai.dragontiles.spells.elementalist.QuickInvoke
import java.util.*

class MainMenuUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage
) : BaseSystem() {

    private val skin = game.skin

    private val rootTable = Table()

    private val newGameLabel = Label("New Game", skin)
    private val quitLabel = Label("Quit", skin)

    override fun initialize() {
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")
        rootTable.add(Label("Dragontiles", skin))
        rootTable.row()
        rootTable.add(newGameLabel)
            .padTop(64f)
        rootTable.row()
        rootTable.add(quitLabel)
        rootTable.row()

        newGameLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val runData = RunData(
                    Random(),
                    Hero(
                        "Elementalist", 60, 60, 0, 40, 17,
                        game.heroSpells.elementalistStarterDeck().toMutableList(), 6,
                        mutableListOf(PiercingStrike()), 3,
                        game.heroSpells.elementalistStarterSorceries().toMutableList(),
                        mutableListOf(Transmuter()),
                        5
                    ),
                    RelicData(RelicData.ALL_RELICS.toMutableList()),
                    PlainsDungeon(),
                    null
                )
                runData.dungeon.generateMap(runData.rng)
                game.screen = EventScreen(game, runData, PlainsStartEvent())
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
