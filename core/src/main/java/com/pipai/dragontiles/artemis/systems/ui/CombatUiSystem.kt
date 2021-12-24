package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.artemis.systems.AnchorSystem
import com.pipai.dragontiles.artemis.systems.animation.CombatAnimationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.gui.SpellComponentList
import com.pipai.dragontiles.potions.PotionTargetType
import com.pipai.dragontiles.spells.FullCastHand
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.findFullCastHand
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.coroutines.resume
import kotlin.math.min

class CombatUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val backStage: Stage,
    private val frontStage: Stage
) : BaseSystem(), InputProcessor {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val config = game.gameConfig
    private val skin = game.skin
    private val tileSkin = game.tileSkin

    private val spells: MutableMap<Int, SpellCard> = mutableMapOf()
    private val spellEntityIds: MutableMap<Int, EntityId> = mutableMapOf()
    private val sideboard: MutableMap<Int, SpellCard> = mutableMapOf()
    private val sideboardEntityIds: MutableMap<Int, EntityId> = mutableMapOf()
    private val sorceries: MutableMap<Int, SpellCard> = mutableMapOf()
    private val sorceryEntityIds: MutableMap<Int, EntityId> = mutableMapOf()
    private val spellComponentList = SpellComponentList(skin, tileSkin)

    private var queryTilesEvent: QueryTilesEvent? = null
    private var queryTileOptionsEvent: QueryTileOptionsEvent? = null
    private var querySwapAmount: Int = 0
    private lateinit var swapChannel: Channel<SwapData>

    private val tilePrevXy: MutableMap<Int, Vector2> = mutableMapOf()
    private val selectedTiles: MutableList<Int> = mutableListOf()
    private val tileOptions: MutableMap<Int, Tile> = mutableMapOf()
    private val swapActiveSpells: MutableList<SpellCard> = mutableListOf()
    private val swapSideboardSpells: MutableList<SpellCard> = mutableListOf()

    companion object {
        private const val ALLOW_HOVER_MOVE = "allowHoverMove"
    }

    private val spacing = 16f
    private val queryTable = Table()
    private val queryLabel = Label("", game.skin, "white")
    private val queryConfirmBtn = TextButton("  Confirm  ", game.skin)

    val layout = CombatUiLayout(config, tileSkin, runData.hero.handSize)

    private val spellCardY = -SpellCard.cardHeight / 3f
    private val leftSpellClosedCenter = Vector2(layout.cardWidth, spellCardY)
    private val leftSpellOpenCenter = Vector2(layout.cardWidth * 3, spellCardY)
    private val leftSpellSwapCenter = Vector2(layout.cardWidth * 3, game.gameConfig.resolution.height.toFloat() / 2)
    private val rightSpellClosedCenter =
        Vector2(game.gameConfig.resolution.width - layout.cardWidth * 2, spellCardY)
    private val rightSpellOpenCenter =
        Vector2(game.gameConfig.resolution.width - layout.cardWidth * 3, spellCardY)
    private val rightSpellSwapCenter = Vector2(
        game.gameConfig.resolution.width - layout.cardWidth * 3,
        game.gameConfig.resolution.height.toFloat() / 2
    )

    var overloaded = false
    private var selectedSpellNumber: Int? = null
    private var mouseFollowEntityId: Int? = null
    private var selectedPotionIndex: Int? = null
    private val givenComponents: MutableList<TileInstance> = mutableListOf()

    private val stateMachine = DefaultStateMachine<CombatUiSystem, CombatUiState>(this, CombatUiState.ROOT)

    private var initted = false

    private val mXy by mapper<XYComponent>()
    private val mClick by mapper<ClickableComponent>()
    private val mAnchor by mapper<AnchorComponent>()
    private val mPath by mapper<PathInterpolationComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<AnchoredLineComponent>()
    private val mMouseFollow by mapper<MouseFollowComponent>()
    private val mDepth by mapper<DepthComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mTargetHighlight by mapper<TargetHighlightComponent>()
    private val mTile by mapper<TileComponent>()
    private val mMutualDestroy by mapper<MutualDestroyComponent>()

    private val sTop by system<TopRowUiSystem>()
    private val sFsTexture by system<FullScreenColorSystem>()
    private val sTileId by system<TileIdSystem>()
    private val sCombat by system<CombatControllerSystem>()
    private val sTooltip by system<TooltipSystem>()
    private val sAnimation by system<CombatAnimationSystem>()
    private val sEvent by system<EventSystem>()
    private val sMap by system<MapUiSystem>()
    private val sAnchor by system<AnchorSystem>()

    override fun initialize() {
        swapChannel = sCombat.controller.api.swapChannel
        sCombat.controller.init()
        val combat = sCombat.combat
        combat.spells.forEachIndexed { index, spell ->
            addSpellCard(index, spell)
        }
        combat.sideboard.forEachIndexed { index, spell ->
            addSpellCardToSideboard(index, spell)
        }
        combat.sorceries.forEachIndexed { index, sorcery ->
            addSorcery(index, sorcery)
        }

        spellComponentList.addClickCallback { selectComponents(it) }
        spellComponentList.addSorceryClickCallback { selectFullCastHand(it) }
        queryTable.setFillParent(true)
        queryTable.add(queryLabel)
            .top()
            .padTop(64f)
            .center()
        queryTable.row()
        queryTable.add()
            .height(game.gameConfig.resolution.height * 3f / 5f)
        queryTable.row()
        queryTable.add(queryConfirmBtn)
            .pad(8f)
        queryTable.row()

        queryConfirmBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                confirm()
            }
        })
    }

    override fun processSystem() {
        if (!initted) {
            initted = true
            openActiveSpells()
            closeSideboardSpells()
        }
        spellEntityIds.forEach { (number, id) ->
            val cXy = mXy.get(id)
            val spellCard = spells[number]!!
            spellCard.x = cXy.x
            spellCard.y = cXy.y
        }
        sideboardEntityIds.forEach { (number, id) ->
            val cXy = mXy.get(id)
            val spellCard = sideboard[number]!!
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
        val spellCard = SpellCard(game, spell, number, game.skin, sCombat.controller.api)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellCard.x = layout.cardWidth * number
        spellCard.y = spellCardY
        frontStage.addActor(spellCard)
        spells[number] = spellCard

        val id = world.create()
        spellEntityIds[number] = id
        mXy.create(id)
        mAnchor.create(id)
        spellCard.data[ALLOW_HOVER_MOVE] = 1
        spellCard.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (stateMachine.currentState == CombatUiState.ROOT) {
                    spellCard.width *= 1.1f
                    spellCard.height *= 1.1f
                    spellCard.toFront()
                }
                sTooltip.addSpell(spell)
                sTooltip.showTooltip(spellCard.x + spellCard.width + 16, spellCard.y)
                if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                    openActiveSpells()
                    closeSideboardSpells()
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                spellCard.width = SpellCard.cardWidth
                spellCard.height = SpellCard.cardHeight
                sTooltip.hideTooltip()
            }
        })
    }

    private fun addSpellCardToSideboard(number: Int, spell: Spell) {
        val spellCard = SpellCard(game, spell, number, game.skin, sCombat.controller.api)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellCard.x = game.gameConfig.resolution.width - layout.cardWidth * 3 + number * layout.cardWidth * 0.8f
        spellCard.y = spellCardY
        frontStage.addActor(spellCard)
        sideboard[number] = spellCard

        val id = world.create()
        sideboardEntityIds[number] = id
        val cXy = mXy.create(id)
        cXy.setXy(spellCard.x, spellCard.y)
        val cAnchor = mAnchor.create(id)
        cAnchor.setXy(spellCard.x, spellCard.y)
        spellCard.data[ALLOW_HOVER_MOVE] = 1
        spellCard.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (stateMachine.currentState == CombatUiState.ROOT) {
                    spellCard.width *= 1.1f
                    spellCard.height *= 1.1f
                    spellCard.toFront()
                }
                sTooltip.addSpell(spell)
                sTooltip.showTooltip(spellCard.x + spellCard.width + 16)
                if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                    openSideboardSpells()
                    closeActiveSpells()
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                spellCard.width = SpellCard.cardWidth
                spellCard.height = SpellCard.cardHeight
                sTooltip.hideTooltip()
            }
        })
    }

    private fun addSorcery(number: Int, sorcery: Sorcery) {
        val spellCard = SpellCard(game, sorcery, number, game.skin, sCombat.controller.api)
        spellCard.addClickCallback(this::spellCardClickCallback)
        spellCard.x = layout.cardWidth * number
        spellCard.y = SpellCard.cardHeight * -3f
        frontStage.addActor(spellCard)
        sorceries[number] = spellCard

        val id = world.create()
        sorceryEntityIds[number] = id
        val cXy = mXy.create(id)
        cXy.setXy(spellCard.x, spellCard.y)
        val cAnchor = mAnchor.create(id)
        cAnchor.setXy(spellCard.x, spellCard.y)
        mActor.create(id).actor = spellCard
        spellCard.data[ALLOW_HOVER_MOVE] = 1
        spellCard.addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (stateMachine.currentState == CombatUiState.ROOT) {
                    spellCard.width *= 1.1f
                    spellCard.height *= 1.1f
                    spellCard.toFront()
                }
                sTooltip.addSpell(sorcery)
                sTooltip.showTooltip(spellCard.x + spellCard.width + 16)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                spellCard.width = SpellCard.cardWidth
                spellCard.height = SpellCard.cardHeight
                sTooltip.hideTooltip()
            }
        })
    }

    fun disable() {
        stateMachine.changeState(CombatUiState.DISABLED)
    }

    fun enable() {
        if (stateMachine.currentState == CombatUiState.DISABLED) {
            stateMachine.changeState(CombatUiState.ROOT)
        }
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
            CombatUiState.POTION_TARGET_SELECTION -> {
                stateMachine.changeState(CombatUiState.ROOT)
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
            Keys.F12 -> {
                scope.launch {
                    sCombat.controller.api.devInstantWin()
                }
            }
            Keys.SPACE -> {
                when (stateMachine.currentState) {
                    CombatUiState.ROOT -> {
                        stateMachine.changeState(CombatUiState.SORCERY_MODE)
                    }
                    CombatUiState.SORCERY_MODE -> {
                        stateMachine.changeState(CombatUiState.ROOT)
                    }
                    else -> {
                    }
                }
            }
            Keys.BACKSPACE -> {
                when (stateMachine.currentState) {
                    CombatUiState.ROOT -> {
                        stateMachine.changeState(CombatUiState.DISABLED)
                        sAnimation.pauseUiMode = true
                        scope.launch {
                            sCombat.controller.endTurn()
                        }
                        return true
                    }
                    else -> {
                    }
                }
            }
            Keys.ENTER -> {
                confirm()
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
            else -> true
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
        when (stateMachine.currentState) {
            CombatUiState.ROOT -> {
                if (!spellCard.powered && spells.values.contains(spellCard)) {
                    val spell = spellCard.getSpell()
                    when (event.button) {
                        Input.Buttons.LEFT -> {
                            if (!(spell is Rune && spell.active)) {
                                spellCard.data[ALLOW_HOVER_MOVE] = 0
                                selectedSpellNumber = spellCard.number
                                stateMachine.changeState(CombatUiState.COMPONENT_SELECTION)
                            }
                        }
                        Input.Buttons.RIGHT -> {
                            if (spell is Rune && spell.active && spell.aspects.none { it is NotManuallyDeactivateable }) {
                                sAnimation.pauseUiMode = true
                                scope.launch {
                                    spell.deactivate(sCombat.controller.api)
                                }
                            }
                        }
                    }
                }
            }
            CombatUiState.QUERY_SWAP -> {
                val isSideboard = sideboard.values.contains(spellCard)
                if (isSideboard) {
                    if (swapSideboardSpells.contains(spellCard)) {
                        spellCard.data[ALLOW_HOVER_MOVE] = 1
                        sAnchor.returnToAnchor(sideboardEntityIds[spellCard.number]!!)
                        swapSideboardSpells.remove(spellCard)
                    } else {
                        if (swapSideboardSpells.size < querySwapAmount) {
                            spellCard.data[ALLOW_HOVER_MOVE] = 0
                            moveSpellToLocation(sideboardEntityIds[spellCard.number]!!, rightSpellSwapCenter)
                            swapSideboardSpells.add(spellCard)
                        }
                    }
                } else {
                    if (swapActiveSpells.contains(spellCard)) {
                        spellCard.data[ALLOW_HOVER_MOVE] = 1
                        sAnchor.returnToAnchor(spellEntityIds[spellCard.number]!!)
                        swapActiveSpells.remove(spellCard)
                    } else {
                        if (swapActiveSpells.size < querySwapAmount) {
                            spellCard.data[ALLOW_HOVER_MOVE] = 0
                            moveSpellToLocation(spellEntityIds[spellCard.number]!!, leftSpellSwapCenter)
                            swapActiveSpells.add(spellCard)
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    fun moveSpellToLocation(id: EntityId, location: Vector2) {
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

        frontStage.addActor(spellComponentList)
        frontStage.keyboardFocus = spellComponentList
        frontStage.scrollFocus = spellComponentList
    }

    private fun displayFullCastHands() {
        val fch = findFullCastHand(sCombat.combat.hand)
        if (fch.isEmpty()) {
            spellComponentList.topText = "None Available"
        } else {
            spellComponentList.topText = "Available Hands"
        }
        spellComponentList.setFullCastOptions(fch)
        spellComponentList.height = min(spellComponentList.prefHeight, SpellCard.cardHeight)
        val position = layout.optionListTlPosition
        spellComponentList.x = position.x - SpellCard.cardWidth
        spellComponentList.y = position.y - spellComponentList.height
        spellComponentList.width = spellComponentList.prefWidth

        frontStage.addActor(spellComponentList)
        frontStage.keyboardFocus = spellComponentList
        frontStage.scrollFocus = spellComponentList
    }

    private fun setSpellComponentOptions(options: List<List<TileInstance>>) {
        spellComponentList.setOptions(options)
        spellComponentList.height = min(spellComponentList.prefHeight, SpellCard.cardHeight)
        val position = layout.optionListTlPosition
        spellComponentList.x = position.x
        spellComponentList.y = position.y - spellComponentList.height
    }

    private fun selectFullCastHand(fullCastHand: FullCastHand) {
        scope.launch {
            sCombat.controller.api.castSorceries(fullCastHand)
        }
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
                        scope.launch {
                            spell.cast(CastParams(listOf()), sCombat.controller.api)
                        }
                    }
                }
            }
            is Rune -> {
                if (spell.active) {
                    sAnimation.pauseUiMode = true
                    scope.launch {
                        spell.deactivate(sCombat.controller.api)
                    }
                } else {
                    sAnimation.pauseUiMode = true
                    spell.fill(components)
                    scope.launch {
                        spell.activate(sCombat.controller.api)
                    }
                }
            }
            is PowerSpell -> {
                spell.fill(components)
                sAnimation.pauseUiMode = true
                scope.launch {
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
            if (stateMachine.currentState == CombatUiState.TARGET_SELECTION) {
                it.target = ev.cEnemy.enemy
                it.update()
            }
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
            when (stateMachine.currentState) {
                CombatUiState.TARGET_SELECTION -> {
                    val spell = getSelectedSpell()
                    if (spell is StandardSpell) {

                        if (spell.targetType == TargetType.SINGLE_ENEMY
                            || spell.targetType == TargetType.SINGLE
                        ) {

                            sAnimation.pauseUiMode = true
                            scope.launch {
                                spell.cast(CastParams(listOf(mEnemy.get(ev.entityId).enemy.id)), sCombat.controller.api)
                            }
                        } else if (spell.targetType == TargetType.AOE) {
                            sAnimation.pauseUiMode = true
                            scope.launch {
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
                CombatUiState.POTION_TARGET_SELECTION -> {
                    sAnimation.pauseUiMode = true
                    scope.launch {
                        sCombat.controller.api.usePotionInCombat(
                            mEnemy.get(ev.entityId).enemy.id,
                            selectedPotionIndex!!
                        )
                    }
                }
                else -> {
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
            CombatUiState.QUERY_TILES -> {
                if (mTile.get(ev.entityId).tile in queryTilesEvent!!.tiles) {
                    if (ev.entityId in selectedTiles) {
                        moveTileBack(ev.entityId)
                    } else {
                        if (selectedTiles.size < queryTilesEvent!!.maxAmount) {
                            moveTileToSelected(ev.entityId)
                        }
                    }
                }
            }
            CombatUiState.QUERY_OPTIONS -> {
                if (ev.entityId in tileOptions) {
                    if (queryTileOptionsEvent!!.minAmount == 1 && queryTileOptionsEvent!!.maxAmount == 1) {
                        queryTileOptionsEvent!!.continuation.resume(listOf(tileOptions[ev.entityId]!!))
                        stateMachine.changeState(CombatUiState.ROOT)
                    }
                }
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
            if (getSelectedSpell().aspects.none { it is PreserveComponentOrder }) {
                givenComponents.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
            }
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
            selectComponents(givenComponents.toList())
        } else {
            readjustHand()
        }
    }

    private fun readjustHand() {
        sAnimation.pauseUiMode = false
        sEvent.dispatch(HandAdjustedEvent(sCombat.combat.hand, sCombat.combat.assigned))
    }

    fun moveTile(entityId: Int, position: Vector2) {
        val cXy = mXy.get(entityId)
        val cPath = mPath.create(entityId)
        cPath.setPath(cXy.toVector2(), position, 0.25f, Interpolation.exp10Out, EndStrategy.REMOVE)
    }

    private fun selectedPosition(index: Int, total: Int): Vector2 {
        val width = game.gameConfig.resolution.width
        val totalSpacing = spacing * (total - 1)
        val totalWidth = totalSpacing + game.tileSkin.width * total
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
            moveTile(tileEntityId, selectedPosition(index, selectedTiles.size))
        }
    }

    fun queryTiles(event: QueryTilesEvent) {
        queryTilesEvent = event
        queryLabel.setText(event.text)
        stateMachine.changeState(CombatUiState.QUERY_TILES)
    }

    fun moveTileToDisplay(entityId: Int) {
        moveTile(entityId, Vector2(game.gameConfig.resolution.width / 2f, game.gameConfig.resolution.height * 2f / 3f))
    }

    fun queryTileOptions(event: QueryTileOptionsEvent) {
        queryTileOptionsEvent = event
        queryLabel.setText(event.text)
        event.displayTile?.let {
            val eid = sTileId.getEntityId(it.id)
            mDepth.get(eid).depth = -2
            moveTileToDisplay(eid)
        }
        event.options.forEachIndexed { index, tile ->
            val entityId = world.create()
            tileOptions[entityId] = tile
            val cSprite = mSprite.create(entityId)
            cSprite.sprite = Sprite(game.tileSkin.regionFor(tile))
            val cDepth = mDepth.create(entityId)
            cDepth.depth = -2
            val cXy = mXy.create(entityId)
            cXy.setXy(selectedPosition(index, event.options.size))
            val cClick = mClick.create(entityId)
            cClick.eventGenerator = { TileClickEvent(entityId, it) }
        }
        stateMachine.changeState(CombatUiState.QUERY_OPTIONS)
    }

    private fun setTileDepth(depth: Int, tiles: List<TileInstance>? = null) {
        world.fetch(allOf(TileComponent::class, DepthComponent::class, SpriteComponent::class)).forEach {
            val cTile = mTile.get(it)
            if (tiles == null || cTile.tile in tiles) {
                val cDepth = mDepth.get(it)
                cDepth.depth = depth
            }
        }
    }

    fun querySwap(amount: Int) {
        querySwapAmount = amount
        queryLabel.setText("Select up to $amount spells to swap")
        stateMachine.changeState(CombatUiState.QUERY_SWAP)
    }

    private fun confirm() {
        when (stateMachine.currentState) {
            CombatUiState.QUERY_TILES -> {
                queryTilesEvent!!.continuation.resume(selectedTiles.map { mTile.get(it).tile })
                stateMachine.revertToPreviousState()
            }
            CombatUiState.QUERY_SWAP -> {
                runBlocking {
                    swapChannel.send(SwapData(
                        swapActiveSpells.map { it.getSpell()!! },
                        swapSideboardSpells.map { it.getSpell()!! }
                    ))
                }
                swapActiveSpells.clear()
                swapSideboardSpells.clear()
                stateMachine.revertToPreviousState()
            }
            else -> {
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

    private fun openActiveSpells() {
        spells.forEach { (number, spellCard) ->
            if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                val entityId = spellEntityIds[number]!!
                val cAnchor = mAnchor.get(entityId)
                cAnchor.x = leftSpellOpenCenter.x + layout.cardWidth * (number - spellEntityIds.size / 2)
                cAnchor.y = leftSpellOpenCenter.y
                sAnchor.returnToAnchor(entityId)
            }
        }
    }

    private fun closeActiveSpells() {
        spells.forEach { (number, spellCard) ->
            if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                val entityId = spellEntityIds[number]!!
                val cAnchor = mAnchor.get(entityId)
                cAnchor.x = leftSpellClosedCenter.x + layout.cardWidth / 4 * (number - spellEntityIds.size / 2)
                cAnchor.y = leftSpellClosedCenter.y
                sAnchor.returnToAnchor(entityId)
                spellCard.toFront()
            }
        }
        queryTable.toFront()
    }

    private fun openSideboardSpells() {
        sideboard.forEach { (number, spellCard) ->
            if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                val entityId = sideboardEntityIds[number]!!
                val cAnchor = mAnchor.get(entityId)
                cAnchor.x = rightSpellOpenCenter.x + layout.cardWidth * (number - sideboardEntityIds.size / 2)
                cAnchor.y = rightSpellOpenCenter.y
                sAnchor.returnToAnchor(entityId)
            }
        }
    }

    private fun closeSideboardSpells() {
        sideboard.forEach { (number, spellCard) ->
            if (spellCard.data[ALLOW_HOVER_MOVE] == 1) {
                val entityId = sideboardEntityIds[number]!!
                val cAnchor = mAnchor.get(entityId)
                cAnchor.x = rightSpellClosedCenter.x + layout.cardWidth / 4 * (number - sideboardEntityIds.size / 2)
                cAnchor.y = rightSpellClosedCenter.y
                sAnchor.returnToAnchor(entityId)
                spellCard.toFront()
            }
        }
        queryTable.toFront()
    }

    private fun moveActiveSpellsFront() {
        spells.values.forEach {
            it.remove()
            frontStage.addActor(it)
        }
    }

    private fun moveActiveSpellsBack() {
        spells.values.forEach {
            it.remove()
            backStage.addActor(it)
        }
    }

    private fun moveSideboardSpellsFront() {
        sideboard.values.forEach {
            it.remove()
            frontStage.addActor(it)
        }
    }

    private fun moveSideboardSpellsBack() {
        sideboard.values.forEach {
            it.remove()
            backStage.addActor(it)
        }
    }

    private fun moveSpellsToAnchor() {
        spellEntityIds.values.forEach {
            sAnchor.returnToAnchor(it)
        }
        sideboardEntityIds.values.forEach {
            sAnchor.returnToAnchor(it)
        }
        sorceryEntityIds.values.forEach {
            sAnchor.returnToAnchor(it)
        }
    }

    fun swapSpells(spellInHand: List<Spell>, spellOnSide: List<Spell>) {
        spellInHand.zip(spellOnSide).forEach { (spell, sideSpell) ->
            val activeSpellCard = findSpellCard(spell)!!
            val sideboardSpellCard = findSideboardCard(sideSpell)!!
            val activeNumber = activeSpellCard.number!!
            val sideNumber = sideboardSpellCard.number!!
            val activeEntityId = spellEntityIds[activeNumber]!!
            val sideEntityId = sideboardEntityIds[sideNumber]!!
            spells[activeNumber] = sideboardSpellCard
            sideboardSpellCard.number = activeNumber
            sideboard[sideNumber] = activeSpellCard
            activeSpellCard.number = sideNumber
            spellEntityIds[activeNumber] = sideEntityId
            sideboardEntityIds[sideNumber] = activeEntityId
            val activeAnchor = mAnchor.get(activeEntityId)
            val sideAnchor = mAnchor.get(sideEntityId)
            val tmpAnchor = activeAnchor.toVector2()
            activeAnchor.setXy(sideAnchor.toVector2())
            sideAnchor.setXy(tmpAnchor)
            activeSpellCard.data[ALLOW_HOVER_MOVE] = 1
            sideboardSpellCard.data[ALLOW_HOVER_MOVE] = 1
            activeSpellCard.clearHoverCallbacks()
            activeSpellCard.addHoverEnterCallback {
                if (it.data[ALLOW_HOVER_MOVE] == 1) {
                    openSideboardSpells()
                    closeActiveSpells()
                }
            }
            sideboardSpellCard.clearHoverCallbacks()
            sideboardSpellCard.addHoverEnterCallback {
                if (it.data[ALLOW_HOVER_MOVE] == 1) {
                    openActiveSpells()
                    closeSideboardSpells()
                }
            }
        }
        openActiveSpells()
        closeSideboardSpells()
    }

    private fun findSpellCard(spell: Spell): SpellCard? {
        return spells.values.find { it.getSpell() == spell }
    }

    private fun findSideboardCard(spell: Spell): SpellCard? {
        return sideboard.values.find { it.getSpell() == spell }
    }

    private fun resetSpellCard(spellCard: SpellCard) {
        spellCard.target = null
        spellCard.data[ALLOW_HOVER_MOVE] = 1
        val spell = spellCard.getSpell()
        if (spell != null && spell is PowerSpell && spell.powered) {
            spellCard.makePowered()
        }
        if (spell == null || !spell.available() || overloaded) {
            spellCard.disable()
        } else {
            spellCard.enable()
        }
        spellCard.update()
    }

    @Subscribe
    fun handlePotionUse(ev: PotionUseEvent) {
        if (stateMachine.currentState == CombatUiState.ROOT) {
            val potion = game.data.getPotion(runData.hero.potionSlots[ev.potionSlotIndex].potionId!!)
            when (potion.targetType) {
                PotionTargetType.NONE -> {
                    runBlocking {
                        sCombat.controller.api.usePotionInCombat(null, ev.potionSlotIndex)
                    }
                }
                PotionTargetType.ENEMY -> {
                    selectedPotionIndex = ev.potionSlotIndex
                    stateMachine.changeState(CombatUiState.POTION_TARGET_SELECTION)
                    val potionId = world.create()
                    val cPotionXy = mXy.create(potionId)
                    cPotionXy.setXy(layout.spellCastPosition)
                    cPotionXy.x -= 100f
                    val cPotionSprite = mSprite.create(potionId)
                    cPotionSprite.sprite =
                        Sprite(game.assets.get(potionAssetPath(potion.assetName), Texture::class.java))
                    cPotionSprite.sprite.setOriginCenter()

                    val id = world.create()
                    mouseFollowEntityId = id
                    mXy.create(id)
                    val cLine = mLine.create(id)
                    cLine.color = Color.GRAY
                    cLine.safeSetAnchor1(id, potionId, mMutualDestroy)
                    cLine.anchor1Offset.set(SpellCard.cardWidth / 2f, SpellCard.cardHeight / 2f)
                    cLine.anchor2 = id
                    mMouseFollow.create(id)

                    mMutualDestroy.create(id).ids.add(potionId)
                }
            }
        }
    }

    enum class CombatUiState : State<CombatUiSystem> {
        ROOT {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.queryTileOptionsEvent = null
                uiSystem.queryTilesEvent = null
                uiSystem.moveSpellsToAnchor()
                uiSystem.selectedTiles.clear()
                uiSystem.spells.forEach { (_, spellCard) ->
                    uiSystem.resetSpellCard(spellCard)
                }
                uiSystem.sideboard.forEach { (_, spellCard) ->
                    uiSystem.resetSpellCard(spellCard)
                }
                uiSystem.givenComponents.clear()
            }

        },
        SORCERY_MODE {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { (_, spellCard) -> spellCard.data[ALLOW_HOVER_MOVE] = 0 }
                uiSystem.spellEntityIds.forEach { (_, eid) ->
                    val cXy = uiSystem.mXy.get(eid)
                    uiSystem.moveSpellToLocation(eid, Vector2(cXy.x, uiSystem.spellCardY - SpellCard.cardHeight))
                }
                uiSystem.sideboard.forEach { (_, spellCard) -> spellCard.data[ALLOW_HOVER_MOVE] = 0 }
                uiSystem.sideboardEntityIds.forEach { (_, eid) ->
                    val cXy = uiSystem.mXy.get(eid)
                    uiSystem.moveSpellToLocation(eid, Vector2(cXy.x, uiSystem.spellCardY - SpellCard.cardHeight))
                }
                uiSystem.sorceryEntityIds.forEach { (_, eid) ->
                    val cXy = uiSystem.mXy.get(eid)
                    uiSystem.moveSpellToLocation(eid, Vector2(cXy.x, uiSystem.spellCardY))
                }
                uiSystem.displayFullCastHands()
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.spellComponentList.remove()
            }
        },
        COMPONENT_SELECTION {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { (number, spellCard) ->
                    if (number == uiSystem.selectedSpellNumber) {
                        uiSystem.moveSpellToLocation(
                            uiSystem.spellCardEntityId(number)!!,
                            uiSystem.layout.spellCastPosition
                        )
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
        TARGET_SELECTION {
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
        POTION_TARGET_SELECTION {
            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.selectedPotionIndex = null
                uiSystem.world.delete(uiSystem.mouseFollowEntityId!!)
                uiSystem.mouseFollowEntityId = null
                uiSystem.removeHighlights()
                uiSystem.sTop.updatePotions()
            }
        },
        QUERY_SWAP {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.values.forEach {
                    val spell = it.getSpell()
                    if (spell !is Rune || !spell.active) {
                        it.enable()
                    }
                }
                uiSystem.sideboard.values.forEach { it.enable() }
                uiSystem.moveActiveSpellsFront()
                uiSystem.moveSideboardSpellsFront()
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.frontStage.addActor(uiSystem.queryTable)
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.sFsTexture.fadeOut(10)
                uiSystem.queryTable.remove()
            }
        },
        QUERY_TILES {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.setTileDepth(-2, uiSystem.queryTilesEvent!!.tiles)
                uiSystem.moveActiveSpellsBack()
                uiSystem.moveSideboardSpellsBack()
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.frontStage.addActor(uiSystem.queryTable)
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.sFsTexture.fadeOut(10)
                uiSystem.queryTable.remove()
                uiSystem.setTileDepth(0)
            }
        },
        QUERY_OPTIONS {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.moveActiveSpellsBack()
                uiSystem.moveSideboardSpellsBack()
                uiSystem.sFsTexture.fadeIn(10)
                uiSystem.frontStage.addActor(uiSystem.queryTable)
            }

            override fun exit(uiSystem: CombatUiSystem) {
                uiSystem.sFsTexture.fadeOut(10)
                uiSystem.tileOptions.forEach { uiSystem.world.delete(it.key) }
                uiSystem.tileOptions.clear()
                uiSystem.queryTable.remove()
                uiSystem.setTileDepth(0)
            }
        },
        DISABLED {
            override fun enter(uiSystem: CombatUiSystem) {
                uiSystem.spells.forEach { (_, spellCard) ->
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

    override fun dispose() {
        scope.cancel()
    }

}
