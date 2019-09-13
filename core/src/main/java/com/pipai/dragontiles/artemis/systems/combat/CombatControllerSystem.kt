package com.pipai.dragontiles.artemis.systems.combat

import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatController

class CombatControllerSystem(private val game: DragonTilesGame, val combat: Combat) : NoProcessingSystem() {

    lateinit var controller: CombatController
        private set

    override fun initialize() {
        controller = CombatController(combat, world, game)
    }

}
