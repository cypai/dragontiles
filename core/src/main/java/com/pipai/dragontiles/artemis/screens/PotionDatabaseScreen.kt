package com.pipai.dragontiles.artemis.screens

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.GroupManager
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.rendering.RenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.PotionDatabaseUiSystem
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import net.mostlyoriginal.api.event.common.EventSystem

class PotionDatabaseScreen(
    game: DragonTilesGame,
) : Screen {

    private val stage = Stage(ScreenViewport(), game.spriteBatch)

    val world: World

    init {
        val config = WorldConfigurationBuilder()
            .with(
                // Managers
                TagManager(),
                GroupManager(),
                EventSystem(),

                TooltipSystem(game, stage),
                FullScreenColorSystem(game),

                InputProcessingSystem(),
            )
            .with(
                -1,
                PotionDatabaseUiSystem(game, stage),
            )
            .with(
                -2,
                RenderingSystem(game)
            )
            .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(PotionDatabaseUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(stage)
        inputProcessor.activateInput()

        StandardScreenInit(world)
            .initialize()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
        world.setDelta(delta)
        world.process()
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
