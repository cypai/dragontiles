package com.pipai.dragontiles.artemis.systems.combat

import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatController
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class CombatControllerSystem(val combat: Combat) : NoProcessingSystem() {

    lateinit var controller: CombatController
        private set

    private val sEvent by system<EventSystem>()

    override fun initialize() {
        controller = CombatController(combat, sEvent)
    }

}
