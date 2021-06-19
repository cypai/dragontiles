package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.combat.StatusAdjustedEvent

class StatusAdjustedAnimation(private val ev: StatusAdjustedEvent) : Animation() {

    private lateinit var sStatus: StatusSystem

    override fun startAnimation() {
        sStatus.handleHeroStatus(ev.heroStatus)
        ev.enemyStatus.forEach { (enemyId, statuses) ->
            sStatus.handleEnemyStatus(enemyId, statuses)
        }
        endAnimation()
    }

}
