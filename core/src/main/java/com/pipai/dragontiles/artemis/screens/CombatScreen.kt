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
import com.pipai.dragontiles.artemis.systems.*
import com.pipai.dragontiles.artemis.systems.animation.CombatAnimationSystem
import com.pipai.dragontiles.artemis.systems.combat.AttackCircleSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.input.ExitInputProcessor
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.CombatRenderingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorRenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.*
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.dungeon.RunData
import net.mostlyoriginal.api.event.common.EventSystem

class CombatScreen(game: DragonTilesGame, runData: RunData, encounter: Encounter) : Screen {

    private val stage = Stage(ScreenViewport(), game.spriteBatch)

    val world: World

    init {
        val combat = Combat(encounter.enemies.map { it.first })
        val config = WorldConfigurationBuilder()
                .with(
                        // Managers
                        TagManager(),
                        GroupManager(),
                        EventSystem(),

                        PathInterpolationSystem(),
                        XyInterpolationSystem(),
                        TimerSystem(),
                        MutualDestroySystem(),

                        CombatControllerSystem(runData, combat),
                        TileIdSystem(),
                        CombatAnimationSystem(game),
                        MouseXySystem(game.gameConfig),
                        AttackCircleSystem(),
                        AttackCircleHoverSystem(game.gameStrings),
                        TooltipSystem(game, stage),

                        InputProcessingSystem(),
                        HoverableSystem(game.gameConfig),
                        ClickableSystem(game.gameConfig))
                .with(-1,
                        CombatUiSystem(game, runData, stage),
                        FullScreenColorRenderingSystem(game))
                .with(-2,
                        CombatRenderingSystem(game),
                        CombatQueryUiSystem(game, runData))
                .with(-3,
                        MapUiSystem(game, stage, runData))
                .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(MouseXySystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(ClickableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(HoverableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(CombatUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(CombatQueryUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(CombatQueryUiSystem::class.java).stage)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(stage)
        inputProcessor.addAlwaysOnProcessor(ExitInputProcessor())
        inputProcessor.activateInput()

        CombatScreenInit(game, world, encounter)
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
