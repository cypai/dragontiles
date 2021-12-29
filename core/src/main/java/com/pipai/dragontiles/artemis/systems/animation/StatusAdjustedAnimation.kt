package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.StatusOverviewAdjustedEvent
import com.pipai.dragontiles.status.Overloaded

class StatusAdjustedAnimation(private val ev: StatusOverviewAdjustedEvent) : Animation() {

    private lateinit var sStatus: StatusSystem
    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        sStatus.handleHeroStatus(ev.heroStatus)
        sUi.overloaded = ev.heroStatus.any { it is Overloaded }
        ev.enemyStatus.forEach { (enemyId, statuses) ->
            sStatus.handleEnemyStatus(enemyId, statuses)
        }
        endAnimation()
    }

}
