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
import com.pipai.dragontiles.artemis.systems.combat.*
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.rendering.RenderingSystem
import com.pipai.dragontiles.artemis.systems.ui.*
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.data.RewardGenerator
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.dungeon.Encounter
import net.mostlyoriginal.api.event.common.EventSystem

class CombatScreen(
    private val game: DragonTilesGame,
    runData: RunData,
    encounter: Encounter,
    rewardConfig: CombatRewardConfig?,
    writeSave: Boolean,
) : Screen {

    private val backStage = Stage(game.uiViewport, game.spriteBatch)
    private val frontStage = Stage(game.uiViewport, game.spriteBatch)

    val world: World

    init {
        val combat = Combat(encounter.enemies.map { it.first })
        if (rewardConfig != null) {
            RewardGenerator().generate(game.data, runData, rewardConfig)
        }
        if (writeSave) {
            // False when loading a save or from a combat event
            game.writeSave()
        }
        val config = WorldConfigurationBuilder()
            .with(
                // Managers
                TagManager(),
                GroupManager(),
                EventSystem(),

                PathInterpolationSystem(),
                AlphaInterpolationSystem(),
                TimerSystem(),
                MutualDestroySystem(),

                CombatControllerSystem(game.data, runData, combat),
                TileIdSystem(),
                CombatantStateSystem(game, frontStage, runData, encounter),
                StatusSystem(game),
                AnchorSystem(),
                CombatAnimationSystem(game),
                MouseXySystem(game),
                TooltipSystem(game, frontStage),
                FullScreenColorSystem(game),
                RewardsSystem(game, runData, frontStage, runData.combatWon),
                MapUiSystem(game, backStage, runData),
                PauseMenuSystem(game, frontStage, runData, false),
                ReqHelpSystem(game, frontStage),

                InputProcessingSystem(),
                HoverableSystem(game),
                ClickableSystem(game)
            )
            .with(
                -1,
                CombatUiSystem(game, runData, backStage, frontStage),
                DeckDisplayUiSystem(game, runData, frontStage),
                TopRowUiSystem(game, runData, frontStage, true),
            )
            .with(
                -2,
                RenderingSystem(game)
            )
            .build()

        world = World(config)

        val inputProcessor = world.getSystem(InputProcessingSystem::class.java)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(MouseXySystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(ClickableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(HoverableSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(RewardsSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(CombatUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(DeckDisplayUiSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(world.getSystem(TooltipSystem::class.java))
        inputProcessor.addAlwaysOnProcessor(frontStage)
        inputProcessor.addAlwaysOnProcessor(backStage)
        inputProcessor.addAlwaysOnProcessor(world.getSystem(PauseMenuSystem::class.java))
        inputProcessor.activateInput()

        CombatScreenInit(game, world, runData, encounter)
            .initialize()
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
