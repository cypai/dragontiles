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
import com.pipai.dragontiles.artemis.systems.HoverableSystem
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.artemis.systems.input.ExitInputProcessor
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.rendering.RenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.*
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.dungeonevents.DungeonEvent
import net.mostlyoriginal.api.event.common.EventSystem

class EventScreen(game: DragonTilesGame, runData: RunData, event: DungeonEvent) : Screen {

    private val backStage = Stage(ScreenViewport(), game.spriteBatch)
    private val frontStage = Stage(ScreenViewport(), game.spriteBatch)

    val world: World

    init {
        game.writeSave()
        val config = WorldConfigurationBuilder()
            .with(
                TagManager(),
                EventSystem(),
                ClickableSystem(game),
                HoverableSystem(game),
                InputProcessingSystem(),
                EventUiSystem(game, backStage, runData, event),
                MapUiSystem(game, backStage, runData),
                TooltipSystem(game, frontStage),
                DeckDisplayUiSystem(game, runData, frontStage),
                FullScreenColorSystem(game),
                PathInterpolationSystem(),
                PauseMenuSystem(game, frontStage, runData, true),
            )
            .with(
                -1,
                RenderingSystem(game),
                TopRowUiSystem(game, runData, frontStage, false),
            )
            .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(frontStage)
        inputProcessor.addAlwaysOnProcessor(backStage)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(ClickableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(HoverableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(DeckDisplayUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(PauseMenuSystem::class.java))
        inputProcessor.activateInput()

        StandardScreenInit(world).initialize()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        backStage.act()
        backStage.draw()
        world.setDelta(delta)
        world.process()
        frontStage.act()
        frontStage.draw()
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
        backStage.dispose()
        frontStage.dispose()
    }
}
