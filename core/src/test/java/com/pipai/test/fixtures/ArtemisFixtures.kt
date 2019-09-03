package com.pipai.test.fixtures

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.GroupManager
import com.artemis.managers.TagManager
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.artemis.systems.XyInterpolationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatAnimationSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.input.InputProcessingSystem
import com.pipai.dragontiles.combat.Combat
import net.mostlyoriginal.api.event.common.EventSystem

fun mockCombatWorld(combat: Combat): World {
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
            .build()

    return World(config)
}