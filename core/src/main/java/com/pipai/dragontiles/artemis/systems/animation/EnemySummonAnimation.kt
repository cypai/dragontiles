package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.combat.EnemySummonEvent

class EnemySummonAnimation(private val ev: EnemySummonEvent) : Animation() {
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.initEnemy(ev.enemy, ev.location)
        endAnimation()
    }
}
