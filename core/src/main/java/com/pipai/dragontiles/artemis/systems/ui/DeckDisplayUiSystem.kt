package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class DeckDisplayUiSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val logger = getLogger()

    companion object {
        private const val SECTION = "SECTION"
    }

    enum class Section {
        QUERY, ACTIVE, SIDEBOARD, SORCERIES
    }

    private val sFsc by system<FullScreenColorSystem>()
    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()

    private lateinit var api: GlobalApi
    private var active = false
    var enableSwap = true
    var disableExit = false

    private val table = Table()
    private val scrollPane = ScrollPane(table)
    private val topLabel = Label("", game.skin, "white")
    private val dragAndDrop = DragAndDrop()
    private val queryFilterBtn = TextButton("", game.skin)
    private val skipBtn = TextButton("  Skip  ", game.skin)
    private val colspan = 6

    override fun initialize() {
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat() - 40f
        api = GlobalApi(game.data, game.assets, runData, sEvent)

        skipBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                disableExit = false
                deactivate()
            }
        })
    }

    fun isShowing() = active

    fun updateStandardDisplay(spellFilter: (Spell) -> Boolean, enableSwapDnd: Boolean) {
        stage.scrollFocus = scrollPane
        table.clearChildren()
        topLabel.setText("Current Spellbook")
        table.add(topLabel).colspan(colspan)
        table.row()
        addStandardDisplay(
            spellFilter,
            { _, _, _ -> },
            enableSwapDnd,
            { _, _, _ -> },
            enableSwapDnd,
            { _, _, _ -> },
        )
    }

    private fun addStandardDisplay(
        spellFilter: (Spell) -> Boolean,
        spellsOnClick: (Spell, Section, Int) -> Unit,
        spellsEnableSwapDnd: Boolean,
        sideboardOnClick: (Spell, Section, Int) -> Unit,
        sideboardEnableSwapDnd: Boolean,
        sorceriesOnClick: (Spell, Section, Int) -> Unit,
    ) {
        addSectionHeader("Starting Active Spells (Max ${runData.hero.spellsSize})")
        addSpellsInSection(
            runData.hero.generateSpells(game.data)
                .mapIndexed { index, spell -> IndexedSpell(spell, index) }
                .filter { spellFilter.invoke(it.spell) },
            spellsOnClick,
            spellsEnableSwapDnd,
            Section.ACTIVE
        )
        addSectionHeader("Sideboard Spells (Max ${runData.hero.sideboardSize})")
        if (runData.hero.sideboard.isEmpty()) {
            addSectionHeader("No spells in sideboard")
        } else {
            addSpellsInSection(
                runData.hero.generateSideboard(game.data)
                    .mapIndexed { index, spell -> IndexedSpell(spell, index) }
                    .filter { spellFilter.invoke(it.spell) },
                sideboardOnClick,
                sideboardEnableSwapDnd,
                Section.SIDEBOARD
            )
        }
        addSectionHeader("Sorceries (Max ${runData.hero.sorceriesSize})")
        if (runData.hero.sorceries.isEmpty()) {
            addSectionHeader("No sorceries")
        } else {
            addSpellsInSection(
                runData.hero.generateSorceries(game.data)
                    .mapIndexed { index, spell -> IndexedSpell(spell, index) }
                    .filter { spellFilter.invoke(it.spell) },
                sorceriesOnClick,
                false,
                Section.SORCERIES
            )
        }
    }

    @Subscribe
    fun handleReplaceQuery(ev: ReplaceSpellQueryEvent) {
        disableExit = true
        queryReplace(ev.spell)
        activate()
    }

    fun queryReplace(spell: Spell) {
        table.clearChildren()
        topLabel.setText("Not enough spell slots, choose a spell to replace")
        table.add(topLabel).colspan(colspan)
        table.row()
        addSectionHeader("New Spell")
        table.add(SpellCard(game, spell, null, game.skin, null))
            .width(SpellCard.cardWidth)
            .height(SpellCard.cardHeight)
            .colspan(colspan)
        table.row()
        addStandardDisplay(
            { if (spell is Sorcery) it is Sorcery else it !is Sorcery },
            { _, section, index -> if (spell !is Sorcery) replaceSpell(spell, section, index) },
            false,
            { _, section, index -> if (spell !is Sorcery) replaceSpell(spell, section, index) },
            false,
            { _, section, index -> if (spell is Sorcery) replaceSpell(spell, section, index) },
        )
        table.add(skipBtn)
            .colspan(colspan)
        table.row()
    }

    @Subscribe
    fun handleUpgradeQuery(ev: UpgradeSpellQueryEvent) {
        disableExit = true
        queryUpgrade(true, ev.upgrade, ev.type, ev.upgradeCallback, ev.skipCallback)
        activate()
    }

    fun queryUpgrade(
        useFilter: Boolean,
        upgrade: SpellUpgrade,
        type: DeckQueryType,
        upgradeCallback: () -> Unit,
        skipCallback: () -> Unit
    ) {
        table.clearChildren()
        topLabel.setText("Choose a spell to upgrade:")
        table.add(topLabel).colspan(colspan)
        table.row()
        val localization = game.gameStrings.nameDescLocalization(upgrade.id)
        table.add(Label("${localization.name}: ${localization.description}", game.skin, "white")).colspan(colspan)
        table.row()
        if (useFilter) {
            queryFilterBtn.setText("  Show all  ")
            queryFilterBtn.clearListeners()
            queryFilterBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    queryUpgrade(false, upgrade, type, upgradeCallback, skipCallback)
                }
            })
        } else {
            queryFilterBtn.setText("  Show upgradable only  ")
            queryFilterBtn.clearListeners()
            queryFilterBtn.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    queryUpgrade(true, upgrade, type, upgradeCallback, skipCallback)
                }
            })
        }
        table.add(queryFilterBtn)
            .colspan(colspan)
        table.row()
        addStandardDisplay(
            if (useFilter) {
                { upgrade.canUpgrade(it) && it.getUpgrades().size < 2 }
            } else {
                { true }
            },
            { clickedSpell, section, index ->
                onSpellUpgradeClick(
                    upgrade,
                    clickedSpell,
                    index,
                    section
                ); upgradeCallback()
            },
            false,
            { clickedSpell, section, index ->
                onSpellUpgradeClick(
                    upgrade,
                    clickedSpell,
                    index,
                    section
                ); upgradeCallback()
            },
            false,
            { _, _, _ -> },
        )
        skipBtn.setText(
            when (type) {
                DeckQueryType.SKIPPABLE -> "  Skip  "
                DeckQueryType.CANCELABLE -> "  Cancel  "
            }
        )
        table.add(skipBtn)
            .colspan(colspan)
        table.row()
    }

    private fun onSpellUpgradeClick(upgrade: SpellUpgrade, spell: Spell, index: Int, section: Section) {
        if (upgrade.canUpgrade(spell)) {
            when (section) {
                Section.ACTIVE -> api.upgradeSpellAtIndex(index, upgrade)
                Section.SIDEBOARD -> api.upgradeSideboardAtIndex(index, upgrade)
                Section.SORCERIES -> api.upgradeSorceryAtIndex(index, upgrade)
                else -> {}
            }
            disableExit = false
            deactivate()
        }
    }

    private fun replaceSpell(newSpell: Spell, section: Section, index: Int) {
        when (section) {
            Section.ACTIVE -> {
                api.removeSpellAtIndex(index)
                api.addSpellToDeck(newSpell)
            }
            Section.SIDEBOARD -> {
                api.removeSideboardAtIndex(index)
                api.addSpellToSideboard(newSpell)
            }
            Section.SORCERIES -> {
                api.removeSorceryAtIndex(index)
                api.addSpellToDeck(newSpell)
            }
            else -> {
            }
        }
        disableExit = false
        deactivate()
    }

    @Subscribe
    fun handleTransformQuery(ev: TransformSpellQueryEvent) {
        disableExit = true
        queryTransform()
        activate()
    }

    fun queryTransform() {
        table.clearChildren()
        topLabel.setText("Choose a spell to transform:")
        table.add(topLabel).colspan(colspan)
        table.row()
        addStandardDisplay(
            { true },
            { _, section, index -> onSpellTransformClick(section, index) },
            false,
            { _, section, index -> onSpellTransformClick(section, index) },
            false,
            { _, section, index -> onSpellTransformClick(section, index) },
        )
    }

    private fun onSpellTransformClick(section: Section, index: Int) {
        when (section) {
            Section.ACTIVE -> api.removeSpellAtIndex(index)
            Section.SIDEBOARD -> api.removeSideboardAtIndex(index)
            Section.SORCERIES -> api.removeSorceryAtIndex(index)
            else -> {}
        }
        val spell = game.data.getHeroClass(runData.hero.heroClassId).getRandomClassSpells(runData.seed, 1).first()
        api.addSpellToDeck(spell)
        deactivate()
    }

    private fun addSectionHeader(text: String) {
        table.add(Label(text, game.skin, "white")).colspan(colspan)
        table.row()
    }

    private fun addSpellsInSection(
        spells: List<IndexedSpell>,
        onClick: (Spell, Section, Int) -> Unit,
        enableSwapDnd: Boolean,
        section: Section,
    ) {
        var cell: Cell<SpellCard>? = null
        spells.forEachIndexed { i, indexedSpell ->
            val spell = indexedSpell.spell
            if (i % colspan == 0 && i != 0) {
                table.row()
            }
            val spellCard = SpellCard(game, spell, i, game.skin, null)
            spellCard.data[SECTION] = section.ordinal
            cell = table.add(spellCard)
                .prefWidth(SpellCard.cardWidth)
                .prefHeight(SpellCard.cardHeight)
                .pad(10f)
            spellCard.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addSpell(spell)
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }

                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onClick(spell, section, indexedSpell.index)
                }
            })
            if (enableSwapDnd) {
                dragAndDrop.setDragActorPosition(SpellCard.cardWidth / 2f, SpellCard.cardHeight / 2f)
                dragAndDrop.addSource(object : DragAndDrop.Source(spellCard) {
                    override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                        val payload = DragAndDrop.Payload()
                        payload.`object` = spellCard
                        payload.dragActor = spellCard
                        return payload
                    }

                    override fun dragStop(
                        event: InputEvent?,
                        x: Float,
                        y: Float,
                        pointer: Int,
                        payload: DragAndDrop.Payload?,
                        target: DragAndDrop.Target?
                    ) {
                        if (target == null) {
                            updateStandardDisplay({ true }, enableSwapDnd)
                        }
                    }
                })
                dragAndDrop.addTarget(object : DragAndDrop.Target(spellCard) {
                    override fun drag(
                        source: DragAndDrop.Source?,
                        payload: DragAndDrop.Payload?,
                        x: Float,
                        y: Float,
                        pointer: Int
                    ): Boolean {
                        return true
                    }

                    override fun drop(
                        source: DragAndDrop.Source?,
                        payload: DragAndDrop.Payload?,
                        x: Float,
                        y: Float,
                        pointer: Int
                    ) {
                        val first = (payload!!.`object` as SpellCard)
                        val second = (this.actor as SpellCard)
                        if (first != second) {
                            logger.info("Swapping ${first.getSpell()!!.id} and ${second.getSpell()!!.id}")
                            if (Section.values()[first.data[SECTION]!!] == Section.ACTIVE) {
                                if (first.data[SECTION] == second.data[SECTION]) {
                                    runData.hero.spells[first.number!!] = second.getSpell()!!.toInstance()
                                    runData.hero.spells[second.number!!] = first.getSpell()!!.toInstance()
                                } else {
                                    runData.hero.spells[first.number!!] = second.getSpell()!!.toInstance()
                                    runData.hero.sideboard[second.number!!] = first.getSpell()!!.toInstance()
                                }
                            } else {
                                if (first.data[SECTION] == second.data[SECTION]) {
                                    runData.hero.sideboard[first.number!!] = second.getSpell()!!.toInstance()
                                    runData.hero.sideboard[second.number!!] = first.getSpell()!!.toInstance()
                                } else {
                                    runData.hero.sideboard[first.number!!] = second.getSpell()!!.toInstance()
                                    runData.hero.spells[second.number!!] = first.getSpell()!!.toInstance()
                                }
                            }
                            updateStandardDisplay({ true }, enableSwapDnd)
                        }
                    }
                })
            }
        }
        if (cell != null && spells.size % colspan != 0) {
            repeat(colspan - spells.size % colspan) {
                table.add()
            }
        }
        table.row()
    }

    fun activate() {
        active = true
        sFsc.fadeIn(0.2f)
        stage.addActor(scrollPane)
        scrollPane.toFront()
        scrollPane.scrollY = 0f
        sEvent.dispatch(DeckDisplayUiEvent(true))
    }

    fun deactivate() {
        active = false
        disableExit = false
        sFsc.fadeOut(0.2f)
        scrollPane.remove()
        sEvent.dispatch(DeckDisplayUiEvent(false))
    }

    private data class IndexedSpell(val spell: Spell, val index: Int)

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.D -> {
                if (active) {
                    if (!disableExit) {
                        deactivate()
                    }
                } else {
                    updateStandardDisplay({ true }, enableSwap)
                    activate()
                }
                return true
            }
            Input.Keys.ESCAPE -> {
                if (active) {
                    if (!disableExit) {
                        deactivate()
                    }
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
