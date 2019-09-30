package com.pipai.dragontiles.artemis.systems.combat

import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatController
import com.pipai.dragontiles.utils.system

class CombatControllerSystem(val combat: Combat) : NoProcessingSystem() {

    lateinit var controller: CombatController
        private set

    private val sAnimation by system<CombatAnimationSystem>()

    override fun initialize() {
        controller = CombatController(combat, sAnimation)
    }

}
