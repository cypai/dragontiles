package com.pipai.dragontiles.artemis.systems.combat

import com.pipai.dragontiles.artemis.systems.ProcessOnceSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatController
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.utils.system
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.mostlyoriginal.api.event.common.EventSystem

class CombatControllerSystem(val runData: RunData, val combat: Combat) : ProcessOnceSystem() {

    lateinit var controller: CombatController
        private set

    private val sEvent by system<EventSystem>()

    override fun initialize() {
        controller = CombatController(runData, combat, sEvent)
    }

    override fun processOnce() {
        GlobalScope.launch {
            controller.runTurn()
        }
    }

}
