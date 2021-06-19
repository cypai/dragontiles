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
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.artemis.systems.animation.CombatAnimationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.combat.HandAdjustedEvent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.gui.SpellComponentList
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.math.min

class CombatUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage
) : BaseSystem(), InputProcessor {

    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()
    private val spellEntityIds: MutableMap<Int, Int> = mutableMapOf()
    private val spellComponentList = SpellComponentList(skin, tileSkin)

    val layout = CombatUiLayout(config, tileSkin, runData.hero.handSize)

    var overloaded = false
    private var selectedSpellNumber: Int? = null
    private var mouseFollowEntityId: Int? = null
    private val givenComponents: MutableList<TileInstance> = mutableListOf()

    private val stateMachine = DefaultStateMachine<CombatUiSystem, CombatUiState>(this, CombatUiState.ROOT)

    private val mXy by mapper<XYComponent>()
    private val mPath by mapper<PathInterpolationComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<AnchoredLineComponent>()
    private val mMouseFollow by mapper<MouseFollowComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mTargetHighlight by mapper<TargetHighlightComponent>()
    private val mTile by mapper<TileComponent>()
    private val mMutualDestroy by mapper<MutualDestroyComponent>()

    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()
    private val sAnimation by system<CombatAnimationSystem>()
    private val sEvent by system<EventSystem>()
    private val sMap by system<MapUiSystem>()

    override fun initialize() {
        runData.hero.spells.forEachIndexed { index, spell ->
            addSpellCard(index, spell)
        }

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

    fun activeTiles(): List<TileInstance> {
        return givenComponents.toList()
    }

    fun spellCardEntityId(index: Int): Int? {
        return spellEntityIds[index]
    }

    private fun addSpellCard(number: Int, spell: Spell) {
        val spellCard = SpellCard(game, spell, number, game.skin, sCombat.controller.api, sTooltip)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellCard.width = layout.cardWidth
        spellCard.height = layout.cardHeight
        spellCard.x = layout.cardWidth * number
        spellCard.y = -SpellCard.cardHeight / 2f
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
                readjustHand()
                true
            }
            CombatUiState.TARGET_SELECTION -> {
                stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                givenComponents.clear()
                readjustHand()
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
                        sAnimation.pauseUiMode = true
                        GlobalScope.launch {
                            sCombat.controller.endTurn()
                        }
                        return true
                    }
                    else -> {
                    }
                }
            }
            Keys.M -> {
                if (sMap.showing) {
                    sMap.hideMap()
                } else {
                    sMap.showMap()
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

    private fun spellCardClickCallback(event: InputEvent, spellCard: SpellCard) {
        if (stateMachine.currentState == CombatUiState.ROOT) {
            val spell = spellCard.getSpell()
            when (event.button) {
                Input.Buttons.LEFT -> {
                    if (!(spell is Rune && spell.active)) {
                        selectedSpellNumber = spellCard.number
                        stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                    }
                }
                Input.Buttons.RIGHT -> {
                    if (spell is Rune && spell.active) {
                        sAnimation.pauseUiMode = true
                        GlobalScope.launch {
                            spell.deactivate(sCombat.controller.api)
                        }
                    }
                }
            }
        }
    }

    private fun moveSpellToLocation(number: Int, location: Vector2) {
        val id = spellEntityIds[number]!!
        val cXy = mXy.get(id)
        val cPath = mPath.create(id)
        cPath.setPath(cXy.toVector2(), location, 0.25f, Interpolation.exp10Out, EndStrategy.REMOVE)
    }

    private fun displaySpellComponents(spellCard: SpellCard) {
        val spell = spellCard.getSpell()!!
        val options = spell.requirement.find(sCombat.combat.hand)
        if (spell.requirement.manualOnly) {
            spellComponentList.topText = "Manual Selection"
        } else {
            if (options.isEmpty()) {
                spellComponentList.topText = "None available"
            } else {
                spellComponentList.topText = "Viable"
            }
        }
        setSpellComponentOptions(options)

        stage.addActor(spellComponentList)
        stage.keyboardFocus = spellComponentList
        stage.scrollFocus = spellComponentList
    }

    private fun setSpellComponentOptions(options: List<List<TileInstance>>) {
        spellComponentList.setOptions(options)
        spellComponentList.height = min(spellComponentList.prefHeight, SpellCard.cardHeight)
        val position = layout.optionListTlPosition
        spellComponentList.x = position.x
        spellComponentList.y = position.y - spellComponentList.height
    }

    private fun selectComponents(components: List<TileInstance>) {
        givenComponents.clear()
        givenComponents.addAll(components)
        readjustHand()
        when (val spell = getSelectedSpell()) {
            is StandardSpell -> {
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
                        sAnimation.pauseUiMode = true
                        GlobalScope.launch {
                            spell.cast(CastParams(listOf()), sCombat.controller.api)
                        }
                    }
                }
            }
            is Rune -> {
                if (spell.active) {
                    sAnimation.pauseUiMode = true
                    GlobalScope.launch {
                        spell.deactivate(sCombat.controller.api)
                    }
                } else {
                    sAnimation.pauseUiMode = true
                    spell.fill(components)
                    GlobalScope.launch {
                        spell.activate(sCombat.controller.api)
                    }
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
        val spell = spellCard.getSpell() as StandardSpell
        when (spell.targetType) {
            TargetType.SINGLE -> {
                highlightEnemies()
            }
            TargetType.AOE -> {
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
    fun handleEnemyClick(ev: EnemyClickEvent) {
        if (ev.button == Input.Buttons.LEFT) {
            val spell = getSelectedSpell()
            if (spell is StandardSpell && stateMachine.currentState == CombatUiState.TARGET_SELECTION) {

                if (spell.targetType == TargetType.SINGLE_ENEMY
                    || spell.targetType == TargetType.SINGLE
                ) {

                    sAnimation.pauseUiMode = true
                    GlobalScope.launch {
                        spell.cast(CastParams(listOf(mEnemy.get(ev.entityId).enemy.id)), sCombat.controller.api)
                    }
                } else if (spell.targetType == TargetType.AOE) {
                    sAnimation.pauseUiMode = true
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
    fun handleTileClick(ev: TileClickEvent) {
        when (stateMachine.currentState) {
            CombatUiState.COMPONENT_SELECTION -> {
                val tile = mTile.get(ev.entityId).tile
                changeGivenTile(tile)
            }
            CombatUiState.TARGET_SELECTION -> {
                stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                val tile = mTile.get(ev.entityId).tile
                changeGivenTile(tile)
            }
            else -> {
            }
        }
    }

    private fun changeGivenTile(tile: TileInstance) {
        if (tile in givenComponents) {
            givenComponents.remove(tile)
        } else {
            givenComponents.add(tile)
            givenComponents.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        }
        val spell = getSelectedSpell()
        if (spell.requirement.manualOnly) {
            val options = spell.requirement.findGiven(sCombat.combat.hand, givenComponents)
            if (options.isNotEmpty()) {
                spellComponentList.topText = "Viable"
                setSpellComponentOptions(options)
            }
        } else {
            spellComponentList.filterOptions(givenComponents)
        }
        if (spell is StandardSpell
            && spell.targetType != TargetType.NONE
            && spell.requirement.reqAmount !is ReqAmount.XAmount
            && spell.requirement.satisfied(givenComponents)
        ) {
            selectComponents(givenComponents)
        } else {
            readjustHand()
        }
    }

    private fun readjustHand() {
        sAnimation.pauseUiMode = false
        sEvent.dispatch(HandAdjustedEvent(sCombat.combat.hand, sCombat.combat.assigned))
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
                    if (cSprite.sprite.boundingRectangle.contains(
                            screenX.toFloat(),
                            config.resolution.height - screenY.toFloat()
                        )
                    ) {
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

    override fun scrolled(amountX: Float, amountY: Float) = false

    enum class CombatUiState : State<CombatUiSystem> {
        ROOT() {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { (number, spellCard) ->
                    uiSystem.moveSpellToLocation(number, uiSystem.layout.spellStartPosition(number))
                    spellCard.target = null
                    spellCard.update()
                    val spell = spellCard.getSpell()
                    if (spell == null || !spell.available() || uiSystem.overloaded) {
                        spellCard.disable()
                    } else {
                        spellCard.enable()
                    }
                }
                uiSystem.givenComponents.clear()
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
                uiSystem.mXy.create(id)
                val spellCardId = uiSystem.spellEntityIds[uiSystem.selectedSpellNumber]!!
                val cLine = uiSystem.mLine.create(id)
                cLine.color = Color.GRAY
                cLine.safeSetAnchor1(id, spellCardId, uiSystem.mMutualDestroy)
                cLine.anchor1Offset.set(SpellCard.cardWidth / 2f, SpellCard.cardHeight / 2f)
                cLine.anchor2 = id
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
