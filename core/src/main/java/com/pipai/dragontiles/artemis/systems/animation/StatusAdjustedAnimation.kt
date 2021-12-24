package com.pipai.dragontiles.artemis.systems.animation

import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.StatusOverviewAdjustedEvent
import com.pipai.dragontiles.status.Overloaded

class StatusAdjustedAnimation(private val ev: StatusOverviewAdjustedEvent) : Animation() {

    private lateinit var sStatus: StatusSystem
    private lateinit var sUi: CombatUiSystem
    private lateinit var sFs: FullScreenColorSystem

    override fun startAnimation() {
        sStatus.handleHeroStatus(ev.heroStatus)
        sUi.overloaded = ev.heroStatus.any { it is Overloaded }
        ev.enemyStatus.forEach { (enemyId, statuses) ->
            sStatus.handleEnemyStatus(enemyId, statuses)
        }
        if (ev.heroStatus.any { it is Overloaded && it.amount > 1 }
            || ev.enemyStatus.values.any { enemyStatus -> enemyStatus.any { it is Overloaded && it.amount > 1 } }) {
            sFs.fadeOut(30, Color.WHITE)
        }
        endAnimation()
    }

}
