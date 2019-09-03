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
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.artemis.systems.XyInterpolationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatAnimationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.input.ExitInputProcessor
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.CombatRenderingSystem
import com.pipai.dragontiles.combat.Combat
import net.mostlyoriginal.api.event.common.EventSystem

class CombatScreen(game: DragonTilesGame, combat: Combat) : Screen {

    private val stage = Stage(ScreenViewport(), game.spriteBatch)

    private val world: World

    init {
        val config = WorldConfigurationBuilder()
                .with(
                        // Managers
                        TagManager(),
                        GroupManager(),
                        EventSystem(),

                        PathInterpolationSystem(),
                        XyInterpolationSystem(),

                        CombatControllerSystem(combat),
                        TileIdSystem(),
                        CombatAnimationSystem(),

                        InputProcessingSystem())
                .with(-1,
                        CombatRenderingSystem(game))
                .build()

        world = World(config)

        val controllerSystem = world.getSystem(CombatControllerSystem::class.java)
        controllerSystem.controller.initCombat()

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(stage)
        inputProcessor.addAlwaysOnProcessor(ExitInputProcessor())
        inputProcessor.activateInput()

        StandardScreenInit(world)
                .initialize()

        controllerSystem.controller.runTurn()
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
