package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.*
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.*
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.utils.choose

class MainMenuUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val startFromOptions: Boolean
) : BaseSystem(), InputProcessor {

    private val skin = game.skin

    private val rootTable = Table()

    private var dbMenu = false

    private val newGameLabel = Label("Start Run", skin)
    private val continueLabel = Label("Continue", skin)
    private val abandonLabel = Label("Abandon Run", skin)
    private val tutorialLabel = Label("Tutorial", skin)
    private val historyLabel = Label("Run History", skin)
    private val databaseLabel = Label("Database", skin)
    private val optionsLabel = Label("Options", skin)
    private val quitLabel = Label("Quit", skin)

    private val spellDbLabel = Label("Spells Database", skin)
    private val relicDbLabel = Label("Relics Database", skin)
    private val potionDbLabel = Label("Potions Database", skin)
    private val sealDbLabel = Label("Seals Database", skin)

    override fun initialize() {
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")

        if (startFromOptions) {
            generateDbTable()
        } else {
            regenerateTable(game.save.currentRun == null)
        }

        newGameLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (game.save.requireTutorial) {
                    game.save.requireTutorial = false
                    game.writeSave()
                }
                val seed = Seed()

                game.music = game.assets.get("assets/binassets/audio/bgm/plains.mp3")
                game.music!!.isLooping = true
                game.music!!.volume = game.save.options.musicVolume
                game.music!!.play()

                val act1Id = "base:dungeons:Plains"
                val act1Map = DungeonMap.generateMap(seed)
                val act1Boss = game.data.getDungeon(act1Id).bossEncounters.choose(seed.dungeonRng())
                val act2Id = "base:dungeons:Mountains"
                val act2Map = DungeonMap.generateMap(seed)
                val act2Boss = game.data.getDungeon(act2Id).bossEncounters.choose(seed.dungeonRng())
                val runData = RunData(
                    Elementalist().generateHero("Elementalist"),
                    DungeonMap(act1Id, DungeonMap(act2Id, null, act2Map, act2Boss.id), act1Map, act1Boss.id),
                    game.data.allRelics().filter { it.rarity != Rarity.SPECIAL && it.rarity != Rarity.STARTER }
                        .map { it.id }.toMutableList(),
                    null,
                    0,
                    GameData.BASE_POTION_CHANCE,
                    false,
                    mutableListOf(),
                    RunHistory(VictoryStatus.IN_PROGRESS, 0, mutableListOf()),
                    seed,
                )
                game.save.currentRun = runData
                game.writeSave()
                game.screen = EventScreen(game, runData, game.data.getDungeon(runData.dungeonMap.dungeonId).startEvent)
            }
        })
        continueLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                loadRun()
            }
        })
        abandonLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.save.currentRun = null
                game.writeSave()
                regenerateTable(true)
            }
        })
        tutorialLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.save.requireTutorial = false
                game.writeSave()
            }
        })
        databaseLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                generateDbTable()
            }
        })
        optionsLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = OptionsScreen(game)
            }
        })
        quitLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Gdx.app.exit()
            }
        })

        spellDbLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = CardDatabaseScreen(game)
            }
        })
        relicDbLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = RelicDatabaseScreen(game)
            }
        })
        potionDbLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = PotionDatabaseScreen(game)
            }
        })
        sealDbLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = SealDatabaseScreen(game)
            }
        })
        stage.addActor(rootTable)
    }

    private fun regenerateTable(newGame: Boolean) {
        dbMenu = false
        rootTable.clearChildren()
        rootTable.add(Label("Dragontiles", skin))
            .pad(32f)
        rootTable.row()
        if (newGame) {
            rootTable.add(newGameLabel)
                .padTop(64f)
            rootTable.row()
        } else {
            rootTable.add(continueLabel)
                .padTop(64f)
            rootTable.row()
            rootTable.add(abandonLabel)
            rootTable.row()
        }
        rootTable.add(tutorialLabel)
        rootTable.row()
        rootTable.add(historyLabel)
        rootTable.row()
        rootTable.add(databaseLabel)
        rootTable.row()
        rootTable.add(optionsLabel)
        rootTable.row()
        rootTable.add(quitLabel)
        rootTable.row()
    }

    private fun generateDbTable() {
        dbMenu = true
        rootTable.clearChildren()
        rootTable.add(Label("Databases", skin))
            .pad(32f)
        rootTable.row()
        rootTable.add(spellDbLabel)
        rootTable.row()
        rootTable.add(relicDbLabel)
        rootTable.row()
        rootTable.add(potionDbLabel)
        rootTable.row()
        rootTable.add(sealDbLabel)
        rootTable.row()
        val backLabel = Label("Back", game.skin)
        backLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                regenerateTable(game.save.currentRun == null)
            }
        })
        rootTable.add(backLabel)
        rootTable.row()
    }

    private fun loadRun() {
        val runData = game.save.currentRun!!
        val dungeon = game.data.getDungeon(runData.dungeonMap.dungeonId)
        game.music = game.assets.get("assets/binassets/audio/bgm/plains.mp3")
        game.music!!.isLooping = true
        game.music!!.volume = game.save.options.musicVolume
        game.music!!.play()
        when (runData.dungeonMap.getCurrentNode().type) {
            MapNodeType.COMBAT -> {
                val floorConfig = runData.runHistory.history.last() as FloorHistory.CombatFloorHistory
                game.screen = CombatScreen(
                    game,
                    runData,
                    dungeon.getEncounter(floorConfig.encounterId)!!,
                    null,
                    false,
                )
            }
            MapNodeType.ELITE -> {
                val floorConfig = runData.runHistory.history.last() as FloorHistory.EliteFloorHistory
                game.screen = CombatScreen(
                    game,
                    runData,
                    dungeon.getEncounter(floorConfig.encounterId)!!,
                    null,
                    false,
                )
            }
            MapNodeType.EVENT -> {
                val floorConfig = runData.runHistory.history.last() as FloorHistory.EventFloorHistory
                game.screen = EventScreen(
                    game,
                    runData,
                    dungeon.dungeonEvents.first { it.id == floorConfig.eventId },
                )
            }
            MapNodeType.TOWN -> {
                game.screen = TownScreen(
                    game,
                    runData,
                )
            }
            MapNodeType.START -> {
                game.screen = EventScreen(game, runData, game.data.getDungeon(runData.dungeonMap.dungeonId).startEvent)
            }
            MapNodeType.BOSS -> {
                game.screen = CombatScreen(
                    game,
                    runData,
                    dungeon.getEncounter(runData.dungeonMap.bossId)!!,
                    null,
                    false,
                )
            }
        }
    }

    override fun processSystem() {
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE && dbMenu) {
            regenerateTable(game.save.currentRun == null)
            return true
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
