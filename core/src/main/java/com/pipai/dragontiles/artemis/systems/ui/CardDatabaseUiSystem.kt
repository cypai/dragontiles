package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.MainMenuScreen
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.system
import com.pipai.dragontiles.utils.withAll

class CardDatabaseUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val topTable = Table()
    private val spellsTable = Table()
    private val scrollPane = ScrollPane(spellsTable)
    private val elementalistButton =
        TextButton("  ${game.gameStrings.nameLocalization(Elementalist()).name}  ", game.skin)
    private val colorlessButton = TextButton("  Colorless  ", game.skin)
    private val topLabel = Label("", game.skin, "white")
    private val colspan = 6

    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat() - 40f

        elementalistButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                updateStandardDisplay(Elementalist().id)
            }
        })
        colorlessButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                updateColorlessDisplay()
            }
        })

        topTable.setFillParent(true)
        topTable.background = game.skin.getDrawable("plains")
        topTable.add(elementalistButton)
            .padTop(16f)
            .padLeft(64f)
            .padRight(64f)
            .padBottom(16f)
            .top()
        topTable.add(colorlessButton)
            .padTop(16f)
            .padLeft(64f)
            .padRight(64f)
            .padBottom(16f)
            .top()
        topTable.row()
        topTable.add(scrollPane)
            .colspan(2)
        stage.addActor(topTable)

        stage.scrollFocus = scrollPane

        updateStandardDisplay(Elementalist().id)
    }

    fun updateStandardDisplay(heroClassId: String) {
        spellsTable.clearChildren()
        val heroClass = game.data.getHeroClass(heroClassId)

        topLabel.setText(game.gameStrings.nameLocalization(heroClass).name)
        spellsTable.add(topLabel).colspan(colspan)
        spellsTable.row()
        val spells = heroClass.starterDeck.withAll(heroClass.spells)
        val sortedSpells = spells.sortedWith(compareBy(
            { it.rarity },
            { it.type },
            { game.gameStrings.nameLocalization(it).name }
        ))
        addSpellsInSection(sortedSpells)
    }

    fun updateColorlessDisplay() {
        spellsTable.clearChildren()

        topLabel.setText("Colorless")
        spellsTable.add(topLabel).colspan(colspan)
        spellsTable.row()
        val spells = game.data.colorlessSpells()
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
                spellsTable.row()
            }
            val spellCard = SpellCard(game, spell, i, game.skin, null)
            cell = spellsTable.add(spellCard)
                .prefWidth(SpellCard.cardWidth)
                .prefHeight(SpellCard.cardHeight)
                .pad(4f)
            spellCard.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addSpell(spell)
                    val screenXy = spellCard.localToScreenCoordinates(Vector2(0f, 0f))
                    sTooltip.showTooltip(screenXy.x + SpellCard.cardWidth + 16, game.gameConfig.resolution.height - screenXy.y)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
        }
        if (cell != null && spells.size % colspan != 0) {
            repeat(colspan - spells.size % colspan) {
                spellsTable.add()
            }
        }
        spellsTable.row()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                game.screen = MainMenuScreen(game, true)
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
