package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.ActorComponent
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.SpellGainedEvent
import com.pipai.dragontiles.combat.GameOverEvent
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.dungeonevents.EventApi
import com.pipai.dragontiles.dungeonevents.EventOption
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.random.Random

class EventUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData,
    private val event: DungeonEvent
) : BaseSystem() {

    private val logger = getLogger()

    private val skin = game.skin

    private val rootTable = Table()

    private val mainTextLabel = Label("", skin)
    private val optionLabels: MutableList<Pair<EventOption, Label>> = mutableListOf()

    private val mXy by mapper<XYComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mPath by mapper<PathInterpolationComponent>()

    private val sMap by system<MapUiSystem>()
    private val sEvent by system<EventSystem>()
    private val sDeckUi by system<DeckDisplayUiSystem>()
    private val sTooltip by system<TooltipSystem>()

    private lateinit var api: EventApi

    private var spellTooltip: SpellCard? = null

    override fun initialize() {
        api = EventApi(game, runData, sEvent, this, sDeckUi, game.gameStrings.eventLocalization(event.id))
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")

        stage.addActor(rootTable)

        event.initialize(api)
        event.onEventStart(api)
    }

    fun allowMapAdvance() {
        sMap.canAdvanceMap = true
    }

    fun showMap() {
        rootTable.remove()
        sMap.showMap()
    }

    fun setMainText(text: String) {
        mainTextLabel.setText(text)
        rebuildTable()
    }

    fun addOption(text: String, option: EventOption) {
        val label = Label(text, skin)
        optionLabels.add(Pair(option, label))
        rebuildTable()
    }

    fun clearOptions() {
        optionLabels.clear()
        rebuildTable()
    }

    fun rebuildTable() {
        rootTable.clearChildren()
        rootTable.add(mainTextLabel)
            .prefHeight(game.gameConfig.resolution.height / 2f)
            .prefWidth(game.gameConfig.resolution.width.toFloat())
            .padLeft(16f)
            .expand()
            .center()
        rootTable.row()
        optionLabels.forEach { optionLabel ->
            val option = optionLabel.first
            val label = optionLabel.second
            label.setText("  " + label.text)
            val labelBox = Container(label)
                .left()
            labelBox.touchable = Touchable.enabled
            if (option.available(api)) {
                labelBox.background = skin.getDrawable("frameDrawable")
                labelBox.addListener(object : ClickListener() {
                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                        labelBox.background = skin.getDrawable("frameDrawableLight")
                        val tooltipItem = option.tooltipItem(api)
                        if (tooltipItem != null) {
                            if (tooltipItem is Spell) {
                                if (spellTooltip != null) {
                                    spellTooltip?.remove()
                                }
                                spellTooltip = SpellCard(game, tooltipItem, null, game.skin, null)
                                spellTooltip!!.x = labelBox.x + game.gameConfig.resolution.width / 2f
                                spellTooltip!!.y = labelBox.y
                                stage.addActor(spellTooltip)
                            } else {
                                sTooltip.addLocalized(tooltipItem)
                            }
                            sTooltip.showTooltip()
                        }
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                        labelBox.background = skin.getDrawable("frameDrawable")
                        spellTooltip?.remove()
                        spellTooltip = null
                        sTooltip.hideTooltip()
                    }

                    override fun clicked(event: InputEvent?, x: Float, y: Float) {
                        spellTooltip?.remove()
                        spellTooltip = null
                        option.onSelect(api)
                    }
                })
            } else {
                labelBox.background = skin.getDrawable("frameDrawableDark")
            }
            rootTable.add(labelBox)
                .prefHeight(64f)
                .prefWidth(game.gameConfig.resolution.width / 2f)
                .pad(16f)
                .left()
                .expand()
            rootTable.row()
        }
    }

    @Subscribe
    fun onSpellGain(ev: SpellGainedEvent) {
        val id = world.create()
        val cXy = mXy.create(id)
        cXy.setXy(
            su(DragonTilesGame.worldWidth() / 2f + 3 * runData.seed.miscRng().nextFloat()),
            su(DragonTilesGame.WORLD_HEIGHT / 2f)
        )
        val spellCard = SpellCard(game, ev.spell, null, game.skin, null)
        val cActor = mActor.create(id)
        cActor.actor = spellCard
        val cPath = mPath.create(id)
        cPath.setPath(
            cXy.toVector2(),
            Vector2(cXy.x, su(-5f)),
            1f,
            Interpolation.pow3In,
            EndStrategy.DESTROY
        )
        stage.addActor(spellCard)
    }

    @Subscribe
    fun onGameOver(ev: GameOverEvent) {
        logger.info("You lost! Game Over.")
        Gdx.app.exit()
    }

    override fun processSystem() {
    }

}
