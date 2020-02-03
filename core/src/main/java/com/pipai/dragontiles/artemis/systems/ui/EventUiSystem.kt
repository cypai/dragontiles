package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.dungeonevents.EventApi
import com.pipai.dragontiles.dungeonevents.EventOption

class EventUiSystem(private val game: DragonTilesGame,
                    private val stage: Stage,
                    runData: RunData,
                    private val event: DungeonEvent) : BaseSystem() {

    private val skin = game.skin

    private val rootTable = Table()

    private val mainTextLabel = Label("", skin)
    private val optionLabels: MutableList<Label> = mutableListOf()

    private val api: EventApi = EventApi(game, runData, this, game.gameStrings.eventLocalization(event.id))

    override fun initialize() {
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")

        stage.addActor(rootTable)

        event.initialize(api)
    }

    fun setMainText(text: String) {
        mainTextLabel.setText(text)
        rebuildTable()
    }

    fun addOption(text: String, option: EventOption) {
        val label = Label(text, skin)
        label.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                option.onSelect(api)
            }
        })
        optionLabels.add(label)
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
        optionLabels.forEach {
            rootTable.add(it)
                    .prefHeight(64f)
                    .prefWidth(game.gameConfig.resolution.width.toFloat())
                    .padLeft(16f)
                    .expand()
                    .center()
            rootTable.row()
        }
    }

    override fun processSystem() {
    }

}
