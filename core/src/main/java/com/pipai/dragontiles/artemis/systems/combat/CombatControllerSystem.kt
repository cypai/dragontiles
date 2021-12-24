package com.pipai.dragontiles.artemis.systems.combat

import com.pipai.dragontiles.artemis.systems.ProcessOnceSystem
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.combat.CombatController
import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.utils.system
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.mostlyoriginal.api.event.common.EventSystem

class CombatControllerSystem(val gameData: GameData, val runData: RunData, val combat: Combat) : ProcessOnceSystem() {

    private val scope = CoroutineScope(Dispatchers.Default)

    lateinit var controller: CombatController
        private set

    private val sEvent by system<EventSystem>()

    override fun initialize() {
        controller = CombatController(gameData, runData, combat, sEvent)
    }

    override fun processOnce() {
        scope.launch {
            controller.runTurn()
        }
    }

    override fun dispose() {
        scope.cancel()
    }

}
