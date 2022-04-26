package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
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
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.dungeonevents.EventApi
import com.pipai.dragontiles.dungeonevents.EventOption
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe
import kotlin.random.Random

class EventUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData,
    private val event: DungeonEvent
) : BaseSystem() {

    private val skin = game.skin

    private val rootTable = Table()

    private val mainTextLabel = Label("", skin)
    private val optionLabels: MutableList<Pair<EventOption, Label>> = mutableListOf()

    private val mXy by mapper<XYComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mPath by mapper<PathInterpolationComponent>()

    private val sMap by system<MapUiSystem>()
    private val sEvent by system<EventSystem>()

    private lateinit var api: EventApi

    override fun initialize() {
        api = EventApi(game, runData, sEvent, this, game.gameStrings.eventLocalization(event.id))
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
        val actualText = if (option.additionalText(api).isBlank()) {
            text
        } else {
            "$text (${option.additionalText(api)})"
        }
        val label = Label(actualText, skin)
        label.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                option.onSelect(api)
            }
        })
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
            labelBox.background = skin.getDrawable("frameDrawable")
            labelBox.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    labelBox.background = skin.getDrawable("frameDrawableLight")
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    labelBox.background = skin.getDrawable("frameDrawable")
                }

                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    option.onSelect(api)
                }
            })
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
        cXy.setXy(DragonTilesGame.worldWidth() / 2f, DragonTilesGame.WORLD_HEIGHT / 2f)
        val cActor = mActor.create(id)
        cActor.actor = SpellCard(game, ev.spell, null, game.skin, null)
        val cPath = mPath.create(id)
        cPath.setPath(
            cXy.toVector2(),
            Vector2(DragonTilesGame.worldWidth() / 2f, -5f),
            0.8f,
            Interpolation.exp5In,
            EndStrategy.DESTROY
        )
    }

    override fun processSystem() {
    }

}
