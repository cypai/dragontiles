package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.ReplaceSpellQueryEvent
import com.pipai.dragontiles.artemis.events.TransformSpellQueryEvent
import com.pipai.dragontiles.artemis.events.UpgradeSpellQueryEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.sorceries.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellUpgrade
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

    private lateinit var api: GlobalApi
    private var active = false
    var enableSwap = true
    var disableExit = false

    private val table = Table()
    private val scrollPane = ScrollPane(table)
    private val topLabel = Label("", game.skin, "white")
    private val dragAndDrop = DragAndDrop()

    override fun initialize() {
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat() - 40f
        api = GlobalApi(runData, sEvent)
    }

    fun updateStandardDisplay(enableSwapDnd: Boolean) {
        table.clearChildren()
        topLabel.setText("Current Spellbook")
        table.add(topLabel).colspan(6)
        table.row()
        addStandardDisplay(
            { _, _ -> },
            enableSwapDnd,
            { _, _ -> },
            enableSwapDnd,
            { _, _ -> },
        )
    }

    private fun addStandardDisplay(
        spellsOnClick: (Spell, Section) -> Unit,
        spellsEnableSwapDnd: Boolean,
        sideboardOnClick: (Spell, Section) -> Unit,
        sideboardEnableSwapDnd: Boolean,
        sorceriesOnClick: (Spell, Section) -> Unit,
    ) {
        addSectionHeader("Starting Active Spells (Max ${runData.hero.spellsSize})")
        addSpellsInSection(runData.hero.spells, spellsOnClick, spellsEnableSwapDnd, Section.ACTIVE)
        if (runData.hero.sideDeck.isNotEmpty()) {
            addSectionHeader("Sideboard Spells (Max ${runData.hero.sideDeckSize})")
            addSpellsInSection(runData.hero.sideDeck, sideboardOnClick, sideboardEnableSwapDnd, Section.SIDEBOARD)
        }
        if (runData.hero.sorceries.isNotEmpty()) {
            addSectionHeader("Sorceries (Max ${runData.hero.sorceriesSize})")
            addSpellsInSection(runData.hero.sorceries, sorceriesOnClick, false, Section.SORCERIES)
        }
    }

    @Subscribe
    fun handleReplaceQuery(ev: ReplaceSpellQueryEvent) {
        queryReplace(ev.spell)
        activate()
    }

    fun queryReplace(spell: Spell) {
        table.clearChildren()
        topLabel.setText("Not enough spell slots, choose a spell to replace (ESC to skip)")
        table.add(topLabel).colspan(6)
        table.row()
        addSectionHeader("New Spell")
        table.add(SpellCard(game, spell, null, game.skin, null))
            .colspan(6)
        table.row()
        addStandardDisplay(
            { clickedSpell, section -> if (spell !is Sorcery) replaceSpell(clickedSpell, spell, section) },
            false,
            { clickedSpell, section -> if (spell !is Sorcery) replaceSpell(clickedSpell, spell, section) },
            false,
            { clickedSpell, section -> if (spell is Sorcery) replaceSpell(clickedSpell, spell, section) },
        )
    }

    @Subscribe
    fun handleUpgradeQuery(ev: UpgradeSpellQueryEvent) {
        queryUpgrade(ev.upgrade)
        activate()
    }

    fun queryUpgrade(upgrade: SpellUpgrade) {
        table.clearChildren()
        topLabel.setText("Choose a spell to upgrade (ESC to skip): ${upgrade.name}")
        table.add(topLabel).colspan(6)
        table.row()
        addStandardDisplay(
            { clickedSpell, _ -> onSpellUpgradeClick(upgrade, clickedSpell) },
            false,
            { clickedSpell, _ -> onSpellUpgradeClick(upgrade, clickedSpell) },
            false,
            { _, _ -> },
        )
    }

    private fun onSpellUpgradeClick(upgrade: SpellUpgrade, spell: Spell) {
        if (upgrade.canUpgrade(spell)) {
            spell.upgrade(upgrade)
            disableExit = false
            deactivate()
        }
    }

    private fun replaceSpell(originalSpell: Spell, newSpell: Spell, section: Section) {
        when (section) {
            Section.ACTIVE -> {
                runData.hero.spells.remove(originalSpell)
                api.addSpellToDeck(newSpell)
            }
            Section.SIDEBOARD -> {
                runData.hero.sideDeck.remove(originalSpell)
                api.addSpellToSideboard(newSpell)
            }
            Section.SORCERIES -> {
                runData.hero.sorceries.remove(originalSpell)
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
        table.add(topLabel).colspan(6)
        table.row()
        addStandardDisplay(
            { clickedSpell, _ -> onSpellTransformClick(clickedSpell) },
            false,
            { clickedSpell, _ -> onSpellTransformClick(clickedSpell) },
            false,
            { _, _ -> },
        )
    }

    private fun onSpellTransformClick(spell: Spell) {
        api.removeSpell(spell)
        api.addSpellToDeck(game.heroSpells.getRandomClassSpells(runData, 1).first())
        deactivate()
    }

    private fun addSectionHeader(text: String) {
        table.add(Label(text, game.skin, "white")).colspan(6)
        table.row()
    }

    private fun addSpellsInSection(
        spells: List<Spell>,
        onClick: (Spell, Section) -> Unit,
        enableSwapDnd: Boolean,
        section: Section,
    ) {
        var cell: Cell<SpellCard>? = null
        spells.forEachIndexed { i, spell ->
            if (i % 6 == 0 && i != 0) {
                table.row()
            }
            val spellCard = SpellCard(game, spell, null, game.skin, null)
            spellCard.data[SECTION] = section.ordinal
            cell = table.add(spellCard)
                .prefWidth(SpellCard.cardWidth)
                .prefHeight(SpellCard.cardHeight)
                .pad(10f)
            spellCard.addClickCallback { _, _ -> onClick(spell, section) }
            if (enableSwapDnd) {
                dragAndDrop.setDragActorPosition(SpellCard.cardWidth / 2f, -SpellCard.cardHeight / 2f)
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
                            updateStandardDisplay(enableSwapDnd)
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
                            logger.info("Swapping ${first.getSpell()!!.strId} and ${second.getSpell()!!.strId}")
                            if (Section.values()[first.data[SECTION]!!] == Section.ACTIVE) {
                                if (first.data[SECTION] == second.data[SECTION]) {
                                    val index1 = runData.hero.spells.indexOf(first.getSpell())
                                    val index2 = runData.hero.spells.indexOf(second.getSpell())
                                    runData.hero.spells[index1] = second.getSpell()!!
                                    runData.hero.spells[index2] = first.getSpell()!!
                                } else {
                                    val index1 = runData.hero.spells.indexOf(first.getSpell())
                                    val index2 = runData.hero.sideDeck.indexOf(second.getSpell())
                                    runData.hero.spells[index1] = second.getSpell()!!
                                    runData.hero.sideDeck[index2] = first.getSpell()!!
                                }
                            } else {
                                if (first.data[SECTION] == second.data[SECTION]) {
                                    val index1 = runData.hero.sideDeck.indexOf(first.getSpell())
                                    val index2 = runData.hero.sideDeck.indexOf(second.getSpell())
                                    runData.hero.sideDeck[index1] = second.getSpell()!!
                                    runData.hero.sideDeck[index2] = first.getSpell()!!
                                } else {
                                    val index1 = runData.hero.sideDeck.indexOf(first.getSpell())
                                    val index2 = runData.hero.spells.indexOf(second.getSpell())
                                    runData.hero.sideDeck[index1] = second.getSpell()!!
                                    runData.hero.spells[index2] = first.getSpell()!!
                                }
                            }
                            updateStandardDisplay(enableSwapDnd)
                        }
                    }
                })
            }
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
        scrollPane.toFront()
        scrollPane.scrollY = 0f
    }

    fun deactivate() {
        active = false
        disableExit = false
        sFsc.fadeOut(10)
        scrollPane.remove()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.D -> {
                if (active) {
                    deactivate()
                } else {
                    updateStandardDisplay(enableSwap)
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
