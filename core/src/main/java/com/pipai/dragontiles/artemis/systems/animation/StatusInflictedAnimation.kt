package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.components.AlphaInterpolationComponent
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.systems.combat.StatusSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.StatusOverviewAdjustedEvent
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class StatusInflictedAnimation(private val status: Status) : Animation() {
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mAlpha: ComponentMapper<AlphaInterpolationComponent>

    override fun startAnimation() {
        when (val combatant = status.combatant!!) {
            is Combatant.HeroCombatant -> {

            }
            is Combatant.EnemyCombatant -> {
                val entityId =
                    world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == combatant.enemy }
                val cAlpha = mAlpha.create(entityId)
                cAlpha.set(0.5f, 0f, 0.5f, Interpolation.linear, EndStrategy.DESTROY)
                cAlpha.onEndpoint = { endAnimation() }
            }
        }
    }

}
