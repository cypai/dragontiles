package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell

class CombatUiSystem(private val game: DragonTilesGame,
                     private val stage: Stage) : NoProcessingSystem(), InputProcessor {

    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val rootTable = Table()
    private val mainTable = Table()
    private val topRow = Table()
    private val spellRow = Table()

    private val hpLabel = Label("80/80", skin)
    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()

    private var selectedSpellNumber: Int? = null

    private val stateMachine = DefaultStateMachine<CombatUiSystem, CombatUiState>(this, CombatUiState.ROOT)

    init {
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        val leftSide = 2 * config.resolution.handSideBuffer + config.resolution.handMaxSize
        rootTable.add()
                .width(leftSide)
                .height(config.resolution.height.toFloat())
        rootTable.add(mainTable)
                .width(config.resolution.width - leftSide)
                .height(config.resolution.height.toFloat())
        mainTable.background(game.skin.getDrawable("frameDrawable"))

        topRow.add(Label("Elementalist", skin))
                .width(160f)
        topRow.add(hpLabel)
        mainTable.add(topRow)
                .left()
                .padBottom(16f)
        mainTable.row()

        for (i in 1..9) {
            addSpellCard(i)
            if (i % 3 == 0) {
                spellRow.row()
            }
        }
        mainTable.add(spellRow)
        mainTable.row()
    }

    private fun addSpellCard(number: Int) {
        val spellCard = SpellCard(null, number, game.skin, tileSkin)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellRow.add(spellCard)
                .minWidth(spellCard.width)
                .minHeight(spellCard.height)
        spells[number] = spellCard
    }

    fun setSpell(number: Int, spell: Spell) {
        spells[number]?.setSpell(spell)
    }

    fun disable() {
        stateMachine.changeState(CombatUiState.DISABLED)
    }

    fun enable() {
        stateMachine.changeState(CombatUiState.ROOT)
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Keys.ESCAPE) {
            when (stateMachine.currentState) {
                CombatUiState.SPELL_SELECTED -> {
                    stateMachine.changeState(CombatUiState.ROOT)
                    return true
                }
                else -> {
                }
            }
        }

        return when (stateMachine.currentState) {
            CombatUiState.ROOT -> {
                selectSpell(keycode)
            }
            else -> false
        }
    }

    private fun selectSpell(keycode: Int): Boolean {
        val spellNumber = when (keycode) {
            Keys.NUM_1 -> 1
            Keys.NUM_2 -> 2
            Keys.NUM_3 -> 3
            Keys.NUM_4 -> 4
            Keys.NUM_5 -> 5
            Keys.NUM_6 -> 6
            Keys.NUM_7 -> 7
            Keys.NUM_8 -> 8
            Keys.NUM_9 -> 9
            else -> null
        }
        val spellCard = spellNumber?.let { spells[spellNumber] }
        val spell = spellCard?.getSpell()
        if (spell != null) {
            selectedSpellNumber = spellNumber
            stateMachine.changeState(CombatUiState.SPELL_SELECTED)
        }
        return spell != null
    }

    private fun spellCardClickCallback(spellCard: SpellCard) {
        if (stateMachine.currentState == CombatUiState.ROOT) {
            selectedSpellNumber = spellCard.number
            stateMachine.changeState(CombatUiState.SPELL_SELECTED)
        }
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amount: Int) = false

    enum class CombatUiState : State<CombatUiSystem> {
        ROOT() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { _, spellCard ->
                    if (spellCard.getSpell() == null) {
                        spellCard.disable()
                    } else {
                        spellCard.enable()
                    }
                }
            }
        },
        SPELL_SELECTED() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { index, spellCard ->
                    if (index == uiSystem.selectedSpellNumber) {
                        spellCard.enable()
                    } else {
                        spellCard.disable()
                    }
                }
            }
        },
        COMPONENTS_SELECTED(),
        DISABLED() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { _, spellCard ->
                    spellCard.disable()
                }
            }
        };

        override fun enter(uiSystem: CombatUiSystem) {
        }

        override fun exit(uiSystem: CombatUiSystem) {
        }

        override fun onMessage(uiSystem: CombatUiSystem, telegram: Telegram): Boolean {
            return false
        }

        override fun update(uiSystem: CombatUiSystem) {
        }
    }

}
