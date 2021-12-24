package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.MainMenuScreen
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.withAll

class SpellDisplayUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val table = Table()
    private val scrollPane = ScrollPane(table)
    private val topLabel = Label("", game.skin, "white")
    private val colspan = 6

    override fun initialize() {
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat() - 40f

        scrollPane.setFillParent(true)
        stage.addActor(scrollPane)

        updateStandardDisplay("base:hero:Elementalist")
    }

    fun updateStandardDisplay(heroClassId: String) {
        table.clearChildren()
        val heroClass = game.data.getHeroClass(heroClassId)

        topLabel.setText(game.gameStrings.nameLocalization(heroClass).name)
        table.add(topLabel).colspan(colspan)
        table.row()
        val spells = heroClass.starterDeck.withAll(heroClass.spells)
        val sortedSpells = spells.sortedWith(compareBy(
            { it.rarity },
            { it.type },
            { game.gameStrings.nameLocalization(it).name }
        ))
        addSpellsInSection(sortedSpells)
    }

    private fun addSpellsInSection(
        spells: List<Spell>,
    ) {
        var cell: Cell<SpellCard>? = null
        spells.forEachIndexed { i, spell ->
            if (i % colspan == 0 && i != 0) {
                table.row()
            }
            val spellCard = SpellCard(game, spell, i, game.skin, null)
            cell = table.add(spellCard)
                .prefWidth(SpellCard.cardWidth)
                .prefHeight(SpellCard.cardHeight)
                .pad(10f)
        }
        if (cell != null && spells.size % colspan != 0) {
            repeat(colspan - spells.size % colspan) {
                table.add()
            }
        }
        table.row()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                game.screen = MainMenuScreen(game)
                return true
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
