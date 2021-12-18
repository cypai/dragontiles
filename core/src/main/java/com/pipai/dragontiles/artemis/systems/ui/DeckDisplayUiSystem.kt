package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.system

class DeckDisplayUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val sFsc by system<FullScreenColorSystem>()

    private var active = false

    private val table = Table()
    private val scrollPane = ScrollPane(table)
    private val topLabel = Label("", game.skin, "white")

    override fun initialize() {
    }

    fun standardDisplay() {
        table.clearChildren()
        table.add(topLabel).colspan(6)
        table.row()
        addSectionHeader("Starting Active Spells")
        addSpellsInSection(runData.hero.spells)
        if (runData.hero.sideDeck.isNotEmpty()) {
            addSectionHeader("Sideboard Spells")
            addSpellsInSection(runData.hero.sideDeck)
        }
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat()
        activate()
    }

    private fun addSectionHeader(text: String) {
        table.add(Label(text, game.skin, "white")).colspan(6)
        table.row()
    }

    private fun addSpellsInSection(spells: List<Spell>) {
        var cell: Cell<SpellCard>? = null
        spells.forEachIndexed { i, spell ->
            if (i % 6 == 0 && i != 0) {
                table.row()
            }
            cell = table.add(SpellCard(game, spell, null, game.skin, null))
                .pad(10f)
        }
        if (cell != null && spells.size % 6 != 0) {
            repeat(6 - spells.size % 6) {
                table.add()
            }
        }
        table.row()
    }

    fun activate() {
        active = true
        sFsc.fadeIn(10)
        stage.addActor(scrollPane)
        scrollPane.toBack()
    }

    fun deactivate() {
        active = false
        sFsc.fadeOut(10)
        scrollPane.remove()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.D -> {
                if (active) {
                    deactivate()
                } else {
                    standardDisplay()
                }
                return true
            }
            Input.Keys.ESCAPE -> {
                if (active) {
                    deactivate()
                    return true
                }
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

    override fun scrolled(amountX: Float, amountY: Float) = false
}
