package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.gui.SpellComponentList
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.TargetType
import com.pipai.dragontiles.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.min

class CombatUiSystem(private val game: DragonTilesGame,
                     runData: RunData,
                     private val stage: Stage) : NoProcessingSystem(), InputProcessor {

    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val rootTable = Table()
    private val mainTable = Table()
    private val topRow = Table()
    private val spellRow = Table()

    private val hpLabel = Label("${runData.hero.hp}/${runData.hero.hpMax}", skin)
    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()
    private val spellComponentList = SpellComponentList(skin, tileSkin)

    private var selectedSpellNumber: Int? = null
    private var mouseFollowEntityId: Int? = null

    private val stateMachine = DefaultStateMachine<CombatUiSystem, CombatUiState>(this, CombatUiState.ROOT)

    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<LineComponent>()
    private val mMouseFollow by mapper<MouseFollowComponent>()
    private val mSprite by mapper<SpriteComponent>()

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        rootTable.add()
                .width(config.resolution.combatZoneWidth())
                .height(config.resolution.height.toFloat())
        rootTable.add(mainTable)
                .width(config.resolution.width - config.resolution.combatZoneWidth())
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

        spellComponentList.addClickCallback { selectComponents(it) }
    }

    fun setHpRelative(amount: Int) {
        val tokens = hpLabel.text.split("/")
        val hp = tokens[0].toInt()
        val hpMax = tokens[1].toInt()
        setHp(hp + amount, hpMax)
    }

    fun setHp(hp: Int, hpMax: Int) {
        hpLabel.setText("$hp/$hpMax")
    }

    private fun addSpellCard(number: Int) {
        val spellCard = SpellCard(game, null, number, game.skin, sCombat.controller.api, sTooltip)
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
        when (keycode) {
            Keys.ESCAPE -> {
                when (stateMachine.currentState) {
                    CombatUiState.COMPONENT_SELECTION -> {
                        stateMachine.changeState(CombatUiState.ROOT)
                        return true
                    }
                    CombatUiState.TARGET_SELECTION -> {
                        stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                        return true
                    }
                    else -> {
                    }
                }
            }
            Keys.BACKSPACE -> {
                when (stateMachine.currentState) {
                    CombatUiState.ROOT -> {
                        stateMachine.changeState(CombatUiState.DISABLED)
                        GlobalScope.launch {
                            sCombat.controller.endTurn()
                        }
                        return true
                    }
                    else -> {
                    }
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
            stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
        }
        return spell != null
    }

    private fun spellCardClickCallback(spellCard: SpellCard) {
        if (stateMachine.currentState == CombatUiState.ROOT) {
            selectedSpellNumber = spellCard.number
            stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
        }
    }

    private fun displaySpellComponents(spellCard: SpellCard) {
        val spell = spellCard.getSpell()!!
        spellComponentList.setOptions(spell.requirement.find(sCombat.combat.hand))
        val position = spellCard.localToStageCoordinates(Vector2(0f, 0f))
        spellComponentList.x = position.x
        spellComponentList.y = MathUtils.clamp(position.y - spellComponentList.prefHeight,
                48f, position.y)
        spellComponentList.height = min(spellComponentList.prefHeight, position.y - 48f + spellCard.height)

        stage.addActor(spellComponentList)
        stage.keyboardFocus = spellComponentList
        stage.scrollFocus = spellComponentList
    }

    private fun selectComponents(components: List<TileInstance>) {
        val spell = getSelectedSpell()
        when (spell.targetType) {
            TargetType.SINGLE -> {
                spell.fill(components)
                stateMachine.changeState(CombatUiState.TARGET_SELECTION)
            }
            TargetType.AOE -> {
            }
            TargetType.NONE -> {
                spell.fill(components)
                GlobalScope.launch {
                    spell.cast(CastParams(listOf()), sCombat.controller.api)
                }
            }
        }
    }

    private fun getSelectedSpellCard(): SpellCard? {
        return if (selectedSpellNumber == null) {
            null
        } else {
            spells[selectedSpellNumber!!]
        }
    }

    private fun getSelectedSpell() = spells[selectedSpellNumber!!]!!.getSpell()!!

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when (stateMachine.currentState) {
            CombatUiState.TARGET_SELECTION -> {
                world.fetch(allOf(EnemyComponent::class, XYComponent::class, SpriteComponent::class)).forEach {
                    val cSprite = mSprite.get(it)
                    if (cSprite.sprite.boundingRectangle.contains(screenX.toFloat(), config.resolution.height - screenY.toFloat())) {
                        GlobalScope.launch {
                            getSelectedSpell().cast(CastParams(listOf(mEnemy.get(it).enemy)), sCombat.controller.api)
                        }
                        return true
                    }
                }
                return false
            }
            else -> false
        }
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        var updateTarget = false
        when (stateMachine.currentState) {
            CombatUiState.TARGET_SELECTION -> {
                world.fetch(allOf(EnemyComponent::class, XYComponent::class, SpriteComponent::class)).forEach {
                    val cSprite = mSprite.get(it)
                    if (cSprite.sprite.boundingRectangle.contains(screenX.toFloat(), config.resolution.height - screenY.toFloat())) {
                        getSelectedSpellCard()?.target = mEnemy.get(it).enemy
                        updateTarget = true
                    }
                }
                if (!updateTarget) {
                    getSelectedSpellCard()?.target = null
                }
            }
            else -> {
            }
        }
        return false
    }

    override fun scrolled(amount: Int) = false

    enum class CombatUiState : State<CombatUiSystem> {
        ROOT() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { _, spellCard ->
                    spellCard.update()
                    val spell = spellCard.getSpell()
                    if (spell == null || !spell.available()) {
                        spellCard.disable()
                    } else {
                        spellCard.enable()
                    }
                }
            }
        },
        COMPONENT_SELECTION() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { index, spellCard ->
                    if (index == uiSystem.selectedSpellNumber) {
                        spellCard.enable()
                        uiSystem.displaySpellComponents(spellCard)
                    } else {
                        spellCard.disable()
                    }
                }
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.spellComponentList.remove()
            }
        },
        TARGET_SELECTION() {
            override fun enter(uiSystem: CombatUiSystem) {
                val id = uiSystem.world.create()
                uiSystem.mouseFollowEntityId = id
                val cLine = uiSystem.mLine.create(id)
                cLine.color = Color.GRAY
                val spellCard = uiSystem.spells[uiSystem.selectedSpellNumber!!]!!
                cLine.start = spellCard.localToStageCoordinates(Vector2(spellCard.width / 2, spellCard.height / 2))
                cLine.end = Vector2()
                uiSystem.mMouseFollow.create(id)
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.world.delete(uiSystem.mouseFollowEntityId!!)
                uiSystem.mouseFollowEntityId = null
            }
        },
        DISABLED() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { (_, spellCard) ->
                    spellCard.update()
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
