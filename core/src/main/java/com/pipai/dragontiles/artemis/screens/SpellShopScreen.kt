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
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.rendering.RenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.DeckDisplayUiSystem
import com.pipai.dragontiles.artemis.systems.ui.SpellShopUiSystem
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.data.RunData
import net.mostlyoriginal.api.event.common.EventSystem

class SpellShopScreen(game: DragonTilesGame, runData: RunData) : Screen {

    private val stage = Stage(game.uiViewport, game.spriteBatch)
    private val frontStage = Stage(game.uiViewport, game.spriteBatch)

    val world: World

    init {
        val config = WorldConfigurationBuilder()
            .with(
                TagManager(),
                EventSystem(),
                ClickableSystem(game),
                HoverableSystem(game),
                InputProcessingSystem(),
                SpellShopUiSystem(game, stage, runData),
                FullScreenColorSystem(game),
                TooltipSystem(game, frontStage),
            )
            .with(
                -1,
                TopRowUiSystem(game, runData, frontStage, false),
                DeckDisplayUiSystem(game, runData, frontStage),
            )
            .with(
                -2,
                RenderingSystem(game)
            )
            .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(frontStage)
        inputProcessor.addAlwaysOnProcessor(stage)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(DeckDisplayUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(SpellShopUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(ClickableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(HoverableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.activateInput()

        StandardScreenInit(world).initialize()
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.3f, 0.2f, 0.2f, 1f)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act()
        stage.draw()
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
        stage.dispose()
        frontStage.dispose()
    }
}
