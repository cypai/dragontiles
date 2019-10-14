package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.TileClickEvent
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorRenderingSystem
import com.pipai.dragontiles.combat.QueryTilesEvent
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.coroutines.resume

class CombatQueryUiSystem(private val game: DragonTilesGame) : BaseSystem(), InputProcessor {

    private val mPath by mapper<PathInterpolationComponent>()
    private val mXy by mapper<XYComponent>()
    private val mTile by mapper<TileComponent>()

    private val sFsTexture by system<FullScreenColorRenderingSystem>()

    private val tilePrevXy: MutableMap<Int, Vector2> = mutableMapOf()
    private val selectedTiles: MutableList<Int> = mutableListOf()

    private val spacing = 16f

    private val stateMachine = DefaultStateMachine<CombatQueryUiSystem, CombatQueryUiState>(this, CombatQueryUiState.DISABLED)
    private var queryTilesEvent: QueryTilesEvent? = null

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
                .height(game.gameConfig.resolution.height / 2f)
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
        cPath.maxT = 15
        cPath.onEnd = EndStrategy.REMOVE
    }

    private fun selectedPosition(index: Int): Vector2 {
        val width = game.gameConfig.resolution.width
        val totalSpacing = spacing * (selectedTiles.size - 1)
        val totalWidth = totalSpacing + game.tileSkin.width * selectedTiles.size
        val firstX = (width - totalWidth) / 2f
        return Vector2(firstX + index * (spacing + game.tileSkin.width), game.gameConfig.resolution.height / 2f)
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
            moveTile(tileEntityId, selectedPosition(index))
        }
    }

    fun queryTiles(event: QueryTilesEvent) {
        queryTilesEvent = event
        label.setText(event.text)
        stateMachine.changeState(CombatQueryUiState.QUERY_TILES)
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
