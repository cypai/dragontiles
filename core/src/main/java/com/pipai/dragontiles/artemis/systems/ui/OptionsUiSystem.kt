package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.MainMenuScreen

class OptionsUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage
) : BaseSystem() {

    private val options = game.save.options
    private val skin = game.skin
    private val dtskin = game.dtskin

    private val rootTable = Table()

    private val musicVolumeBar = ProgressBar(0f, 100f, 1f, false, dtskin, "volumebar")
    private val soundVolumeBar = ProgressBar(0f, 100f, 1f, false, dtskin, "volumebar")
    private val backLabel = Label("Back", skin)

    override fun initialize() {
        rootTable.setFillParent(true)
        rootTable.background = game.skin.getDrawable("frameDrawable")

        musicVolumeBar.value = options.musicVolume * 100f
        musicVolumeBar.touchable = Touchable.enabled
        soundVolumeBar.value = options.soundVolume * 100f
        soundVolumeBar.touchable = Touchable.enabled

        rootTable.add(Label("Music Volume", skin))
        rootTable.row()
        rootTable.add(musicVolumeBar)
        rootTable.row()
        rootTable.add(Label("Sound Volume", skin))
        rootTable.row()
        rootTable.add(soundVolumeBar)
        rootTable.row()
        rootTable.add(backLabel)
        rootTable.row()

        musicVolumeBar.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                musicVolumeBar.value = x / musicVolumeBar.width * 100f
                options.musicVolume = musicVolumeBar.value / 100f
                return true
            }
            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                musicVolumeBar.value = x / musicVolumeBar.width * 100f
                options.musicVolume = musicVolumeBar.value / 100f
            }
        })

        soundVolumeBar.addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                soundVolumeBar.value = x / soundVolumeBar.width * 100f
                options.soundVolume = soundVolumeBar.value / 100f
                return true
            }
            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                soundVolumeBar.value = x / soundVolumeBar.width * 100f
                options.soundVolume = soundVolumeBar.value / 100f
            }
        })

        backLabel.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.writeSave()
                game.screen = MainMenuScreen(game)
            }
        })

        stage.addActor(rootTable)
    }

    override fun processSystem() {
    }

}
