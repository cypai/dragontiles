package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.gui.SpellComponentList
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.TargetType
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.math.min

class CombatUiSystem(private val game: DragonTilesGame,
                     private val runData: RunData,
                     private val stage: Stage) : BaseSystem(), InputProcessor {

    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val rootTable = Table()
    private val topRow = Table()

    private val hpLabel = Label("${runData.hero.hp}/${runData.hero.hpMax}", skin)
    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()
    private val spellEntityIds: MutableMap<Int, Int> = mutableMapOf()
    private val spellComponentList = SpellComponentList(skin, tileSkin)

    val layout = CombatUiLayout(config, tileSkin, runData.hero.handSize)

    private var selectedSpellNumber: Int? = null
    private var mouseFollowEntityId: Int? = null

    private val stateMachine = DefaultStateMachine<CombatUiSystem, CombatUiState>(this, CombatUiState.ROOT)

    private val mXy by mapper<XYComponent>()
    private val mPath by mapper<PathInterpolationComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mAttackCircle by mapper<AttackCircleComponent>()
    private val mLine by mapper<LineComponent>()
    private val mMouseFollow by mapper<MouseFollowComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mTargetHighlight by mapper<TargetHighlightComponent>()

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        rootTable.setFillParent(true)

        topRow.background = skin.getDrawable("frameDrawable")
        topRow.add(Label("Elementalist", skin))
                .width(260f)
                .pad(8f)
                .padLeft(16f)
                .left()
                .top()
        topRow.add(hpLabel)
                .width(120f)
        topRow.add()
                .expand()

        rootTable.add(topRow)
                .width(config.resolution.width.toFloat())
                .top()
                .left()
        rootTable.row()
        rootTable.add()
                .expand()

        runData.hero.spells.forEachIndexed { index, spell ->
            addSpellCard(index, spell)
        }
        stage.addActor(rootTable)

        spellComponentList.addClickCallback { selectComponents(it) }
    }

    override fun processSystem() {
        spellEntityIds.forEach { (number, id) ->
            val cXy = mXy.get(id)
            val spellCard = spells[number]!!
            spellCard.x = cXy.x
            spellCard.y = cXy.y
        }
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

    private fun addSpellCard(number: Int, spell: Spell) {
        val spellCard = SpellCard(game, spell, number, game.skin, sCombat.controller.api, sTooltip)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellCard.width = layout.cardWidth
        spellCard.height = layout.cardHeight
        spellCard.x = layout.cardWidth * number
        spellCard.y = -spellCard.cardHeight / 2f
        stage.addActor(spellCard)
        spells[number] = spellCard

        val id = world.create()
        spellEntityIds[number] = id
        val cXy = mXy.create(id)
        cXy.setXy(spellCard.x, spellCard.y)
    }

    fun disable() {
        stateMachine.changeState(CombatUiState.DISABLED)
    }

    fun enable() {
        stateMachine.changeState(CombatUiState.ROOT)
    }

    fun setStateBack(): Boolean {
        return when (stateMachine.currentState) {
            CombatUiState.COMPONENT_SELECTION -> {
                stateMachine.changeState(CombatUiState.ROOT)
                true
            }
            CombatUiState.TARGET_SELECTION -> {
                stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                true
            }
            else -> false
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Keys.ESCAPE -> {
                return setStateBack()
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
            Keys.NUM_1 -> 0
            Keys.NUM_2 -> 1
            Keys.NUM_3 -> 2
            Keys.NUM_4 -> 3
            Keys.NUM_5 -> 4
            Keys.NUM_6 -> 5
            Keys.NUM_7 -> 6
            Keys.NUM_8 -> 7
            Keys.NUM_9 -> 8
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

    private fun moveSpellToLocation(number: Int, location: Vector2) {
        val id = spellEntityIds[number]!!
        val cXy = mXy.get(id)
        val cPath = mPath.create(id)
        cPath.clear()
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(location)
        cPath.t = 0
        cPath.maxT = 15
        cPath.interpolation = Interpolation.exp10Out
        cPath.onEnd = EndStrategy.REMOVE
    }

    private fun displaySpellComponents(spellCard: SpellCard) {
        val spell = spellCard.getSpell()!!
        spellComponentList.setOptions(spell.requirement.find(sCombat.combat.hand))
        spellComponentList.height = min(spellComponentList.prefHeight, spellCard.height)
        val position = layout.optionListTlPosition
        spellComponentList.x = position.x
        spellComponentList.y = position.y - spellComponentList.height

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
            TargetType.SINGLE_ENEMY -> {
                spell.fill(components)
                stateMachine.changeState(CombatUiState.TARGET_SELECTION)
            }
            TargetType.SINGLE_CA -> {
                spell.fill(components)
                stateMachine.changeState(CombatUiState.TARGET_SELECTION)
            }
            TargetType.AOE -> {
                spell.fill(components)
                stateMachine.changeState(CombatUiState.TARGET_SELECTION)
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

    private fun highlightTargets() {
        val spellCard = getSelectedSpellCard()!!
        val spell = spellCard.getSpell()!!
        when (spell.targetType) {
            TargetType.SINGLE -> {
                highlightEnemies()
            }
            else -> {
            }
        }
    }

    private fun removeHighlights() {
        world.fetch(allOf(TargetHighlightComponent::class)).forEach {
            mTargetHighlight.remove(it)
        }
    }

    private fun highlightEnemies() {
        world.fetch(allOf(EnemyComponent::class, SpriteComponent::class)).forEach {
            val cSprite = mSprite.get(it)
            val cTargetHighlight = mTargetHighlight.create(it)
            cTargetHighlight.width = cSprite.sprite.width
            cTargetHighlight.height = cSprite.sprite.height
            cTargetHighlight.padding = 8f
            cTargetHighlight.alpha = 0.5f
        }
    }

    @Subscribe
    fun handleEnemyHoverEnter(ev: EnemyHoverEnterEvent) {
        getSelectedSpellCard()?.let {
            it.target = ev.cEnemy.enemy
            it.update()
        }
    }

    @Subscribe
    fun handleEnemyHoverExit(ev: EnemyHoverExitEvent) {
        getSelectedSpellCard()?.let {
            it.target = null
            it.update()
        }
    }

    @Subscribe
    fun handleAttackCircleHoverEnter(ev: AttackCircleHoverEnterEvent) {
        getSelectedSpellCard()?.let {
            it.target = sCombat.controller.api.getCountdownAttack(ev.cAttackCircle.id)
            it.update()
        }
    }

    @Subscribe
    fun handleAttackCircleHoverExit(ev: AttackCircleHoverExitEvent) {
        getSelectedSpellCard()?.let {
            it.target = null
            it.update()
        }
    }

    @Subscribe
    fun handleEnemyClick(ev: EnemyClickEvent) {
        if (ev.button == Input.Buttons.LEFT) {
            val spell = getSelectedSpell()
            if (stateMachine.currentState == CombatUiState.TARGET_SELECTION) {

                if (spell.targetType == TargetType.SINGLE_ENEMY
                        || spell.targetType == TargetType.SINGLE) {

                    GlobalScope.launch {
                        spell.cast(CastParams(listOf(mEnemy.get(ev.entityId).enemy.id)), sCombat.controller.api)
                    }
                } else if (spell.targetType == TargetType.AOE) {
                    GlobalScope.launch {
                        spell.cast(
                                CastParams(sCombat.combat.enemies
                                        .filter { it.hp > 0 }
                                        .map { it.id }
                                        .toList()),
                                sCombat.controller.api)
                    }
                }
            }
        }
    }

    @Subscribe
    fun handleAttackCircleClick(ev: AttackCircleClickEvent) {
        if (ev.button == Input.Buttons.LEFT) {
            val spell = getSelectedSpell()
            if (stateMachine.currentState == CombatUiState.TARGET_SELECTION) {

                if (spell.targetType == TargetType.SINGLE_ENEMY
                        || spell.targetType == TargetType.SINGLE) {

                    GlobalScope.launch {
                        spell.cast(CastParams(listOf(mAttackCircle.get(ev.entityId).id)), sCombat.controller.api)
                    }
                } else if (spell.targetType == TargetType.AOE) {
                    GlobalScope.launch {
                        spell.cast(
                                CastParams(sCombat.combat.enemyAttacks.values.map { it.id }.toList()),
                                sCombat.controller.api)
                    }
                }
            }
        }
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return when (button) {
            Input.Buttons.RIGHT -> {
                setStateBack()
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
                uiSystem.spells.forEach { (number, spellCard) ->
                    uiSystem.moveSpellToLocation(number, uiSystem.layout.spellStartPosition(number))
                    spellCard.target = null
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
                uiSystem.spells.forEach { (number, spellCard) ->
                    if (number == uiSystem.selectedSpellNumber) {
                        uiSystem.moveSpellToLocation(number, uiSystem.layout.spellCastPosition)
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
                uiSystem.getSelectedSpellCard()?.update()
                val id = uiSystem.world.create()
                uiSystem.mouseFollowEntityId = id
                val cLine = uiSystem.mLine.create(id)
                cLine.color = Color.GRAY
                val spellCard = uiSystem.spells[uiSystem.selectedSpellNumber!!]!!
                cLine.start = spellCard.localToStageCoordinates(Vector2(spellCard.width / 2, spellCard.height / 2))
                cLine.end = Vector2()
                uiSystem.mMouseFollow.create(id)
                uiSystem.highlightTargets()
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.world.delete(uiSystem.mouseFollowEntityId!!)
                uiSystem.mouseFollowEntityId = null
                uiSystem.removeHighlights()
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
