package com.pipai.dragontiles.artemis.screens

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.ClickableSystem
import com.pipai.dragontiles.artemis.systems.input.ExitInputProcessor
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.rendering.RenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.*
import com.pipai.dragontiles.dungeon.RunData
import net.mostlyoriginal.api.event.common.EventSystem

class TownScreen(game: DragonTilesGame, runData: RunData, init: Boolean) : Screen {

    private val stage = Stage(ScreenViewport(), game.spriteBatch)

    val world: World

    init {
        val config = WorldConfigurationBuilder()
            .with(
                TagManager(),
                EventSystem(),
                ClickableSystem(game.gameConfig),
                InputProcessingSystem(),
                TownUiSystem(game, runData),
                DeckDisplayUiSystem(game, runData, stage),
                MapUiSystem(game, stage, runData),
                FullScreenColorSystem(game),
                TooltipSystem(game, stage),
            )
            .with(
                -1,
                RenderingSystem(game),
                TopRowUiSystem(game, runData, stage, false)
            )
            .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(stage)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(MapUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(DeckDisplayUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(ClickableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(ExitInputProcessor())
        inputProcessor.activateInput()

        TownScreenInit(game, runData, world, init).initialize()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        world.setDelta(delta)
        world.process()
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun dispose() {
        world.dispose()
        stage.dispose()
    }
}
