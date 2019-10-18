package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.TileClickEvent
import com.pipai.dragontiles.artemis.screens.CombatScreen
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorRenderingSystem
import com.pipai.dragontiles.combat.QueryTileOptionsEvent
import com.pipai.dragontiles.combat.QueryTilesEvent
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.coroutines.resume

class CombatQueryUiSystem(private val game: DragonTilesGame, private val runData: RunData) : BaseSystem(), InputProcessor {

    private val mPath by mapper<PathInterpolationComponent>()
    private val mXy by mapper<XYComponent>()
    private val mTile by mapper<TileComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mClick by mapper<ClickableComponent>()

    private val sFsTexture by system<FullScreenColorRenderingSystem>()
    private val sTileId by system<TileIdSystem>()
    private val sCombat by system<CombatControllerSystem>()
    private val sToolTip by system<TooltipSystem>()

    private val tilePrevXy: MutableMap<Int, Vector2> = mutableMapOf()
    private val selectedTiles: MutableList<Int> = mutableListOf()
    private val tileOptions: MutableMap<Int, Tile> = mutableMapOf()

    private val spacing = 16f

    private val stateMachine = DefaultStateMachine<CombatQueryUiSystem, CombatQueryUiState>(this, CombatQueryUiState.DISABLED)
    private var queryTilesEvent: QueryTilesEvent? = null
    private var queryTileOptionsEvent: QueryTileOptionsEvent? = null

    val stage = Stage()
    private val table = Table()
    private val label = Label("", game.skin, "white")
    private val confirmBtn = TextButton("  Confirm  ", game.skin)

    override fun initialize() {
        table.setFillParent(true)
        table.add(label)
                .top()
                .padTop(64f)
                .center()
        table.row()
        table.add()
                .height(game.gameConfig.resolution.height * 2f / 3f)
        table.row()
        table.add(confirmBtn)
                .pad(8f)
        table.row()

        confirmBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                confirm()
            }
        })
    }

    override fun processSystem() {
        stage.act()
        stage.draw()
    }

    fun moveTile(entityId: Int, position: Vector2) {
        val cXy = mXy.get(entityId)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(position)
        cPath.interpolation = Interpolation.exp10Out
        cPath.t = 0
        cPath.maxT = 15
        cPath.onEnd = EndStrategy.REMOVE
    }

    private fun selectedPosition(index: Int, total: Int): Vector2 {
        val width = game.gameConfig.resolution.width
        val totalSpacing = spacing * (total - 1)
        val totalWidth = totalSpacing + game.tileSkin.width * total
        val firstX = (width - totalWidth) / 2f
        return Vector2(firstX + index * (spacing + game.tileSkin.width), game.gameConfig.resolution.height / 3f)
    }

    fun moveTileToSelected(entityId: Int) {
        tilePrevXy[entityId] = mXy.get(entityId).toVector2()
        selectedTiles.add(entityId)
        reposition()
    }

    fun moveTileBack(entityId: Int) {
        moveTile(entityId, tilePrevXy[entityId]!!)
        selectedTiles.remove(entityId)
        reposition()
    }

    private fun reposition() {
        selectedTiles.forEachIndexed { index, tileEntityId ->
            moveTile(tileEntityId, selectedPosition(index, selectedTiles.size))
        }
    }

    fun queryTiles(event: QueryTilesEvent) {
        queryTilesEvent = event
        label.setText(event.text)
        stateMachine.changeState(CombatQueryUiState.QUERY_TILES)
    }

    fun moveTileToDisplay(entityId: Int) {
        moveTile(entityId, Vector2(game.gameConfig.resolution.width / 2f, game.gameConfig.resolution.height * 2f / 3f))
    }

    fun queryTileOptions(event: QueryTileOptionsEvent) {
        queryTileOptionsEvent = event
        label.setText(event.text)
        event.displayTile?.let {
            moveTileToDisplay(sTileId.getEntityId(it.id))
        }
        event.options.forEachIndexed { index, tile ->
            val entityId = world.create()
            tileOptions[entityId] = tile
            val cSprite = mSprite.create(entityId)
            cSprite.sprite = Sprite(game.tileSkin.regionFor(tile))
            val cXy = mXy.create(entityId)
            cXy.setXy(selectedPosition(index, event.options.size))
            val cClick = mClick.create(entityId)
            cClick.event = TileClickEvent(entityId)
        }
        stateMachine.changeState(CombatQueryUiState.QUERY_OPTIONS)
    }

    @Subscribe
    fun tileClicked(event: TileClickEvent) {
        when (stateMachine.currentState) {
            CombatQueryUiState.QUERY_TILES -> {
                if (mTile.get(event.entityId).tile in queryTilesEvent!!.tiles) {
                    if (event.entityId in selectedTiles) {
                        moveTileBack(event.entityId)
                    } else {
                        moveTileToSelected(event.entityId)
                    }
                }
            }
            CombatQueryUiState.QUERY_OPTIONS -> {
                if (event.entityId in tileOptions) {
                    if (queryTileOptionsEvent!!.minAmount == 1 && queryTileOptionsEvent!!.maxAmount == 1) {
                        queryTileOptionsEvent!!.continuation.resume(listOf(tileOptions[event.entityId]!!))
                        stateMachine.changeState(CombatQueryUiState.DISABLED)
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun confirm() {
        if (stateMachine.currentState != CombatQueryUiState.DISABLED) {
            queryTilesEvent!!.continuation.resume(selectedTiles.map { mTile.get(it).tile })
            stateMachine.changeState(CombatQueryUiState.DISABLED)
        }
    }

    fun generateRewards() {
        stateMachine.changeState(CombatQueryUiState.REWARDS)
        generateRewardsTable(game.heroSpells.generateRewards(runData, 3))
    }

    private fun generateRewardsTable(spells: List<Spell>) {
        val rewardsTable = Table()
        spells.forEach { spell ->
            val spellCard = SpellCard(game, spell, null, game.skin, sCombat.controller.api, sToolTip)
            spellCard.addClickCallback {
                runData.hero.spells.add(spell)
                runData.dungeon.currentFloor += 1
                val encounter = when {
                    runData.dungeon.currentFloor <= 3 -> runData.dungeon.easyEncounter(runData)
                    runData.dungeon.currentFloor <= 9 -> runData.dungeon.standardEncounter(runData)
                    else -> runData.dungeon.bossEncounter(runData)
                }
                game.screen = CombatScreen(game, runData, encounter)
            }
            rewardsTable.add(spellCard)
        }
        label.setText("Rewards! Choose a spell.")

        table.clearChildren()
        table.setFillParent(true)
        table.add(label)
                .top()
                .padTop(64f)
                .center()
        table.row()
        table.add(rewardsTable)
                .height(game.gameConfig.resolution.height * 2f / 3f)
                .width(game.gameConfig.resolution.width * 3f / 4f)
        table.row()

        stage.addActor(table)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ENTER -> {
                confirm()
            }
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(p0: Int) = false

    enum class CombatQueryUiState : State<CombatQueryUiSystem> {
        QUERY_TILES {
            override fun enter(uiSystem: CombatQueryUiSystem) {
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.stage.addActor(uiSystem.table)
            }

            override fun exit(uiSystem: CombatQueryUiSystem) {
                uiSystem.sFsTexture.fadeOut(10)
            }
        },
        QUERY_OPTIONS {
            override fun enter(uiSystem: CombatQueryUiSystem) {
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.stage.addActor(uiSystem.table)
            }

            override fun exit(uiSystem: CombatQueryUiSystem) {
                uiSystem.sFsTexture.fadeOut(10)
                uiSystem.tileOptions.forEach { uiSystem.world.delete(it.key) }
                uiSystem.tileOptions.clear()
            }
        },
        REWARDS {
            override fun enter(uiSystem: CombatQueryUiSystem) {
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.stage.addActor(uiSystem.table)
            }
        },
        DISABLED {
            override fun enter(uiSystem: CombatQueryUiSystem) {
                uiSystem.table.remove()
                uiSystem.tilePrevXy.clear()
                uiSystem.selectedTiles.clear()
            }
        };

        override fun enter(uiSystem: CombatQueryUiSystem) {
        }

        override fun exit(uiSystem: CombatQueryUiSystem) {
        }

        override fun onMessage(uiSystem: CombatQueryUiSystem, telegram: Telegram): Boolean {
            return false
        }

        override fun update(uiSystem: CombatQueryUiSystem) {
        }
    }

}
