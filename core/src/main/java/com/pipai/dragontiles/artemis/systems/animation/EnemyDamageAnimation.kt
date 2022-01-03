package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.ParticleEffectComponent
import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class EnemyDamageAnimation(private val enemy: Enemy, private val amount: Int) : Animation() {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mParticle: ComponentMapper<ParticleEffectComponent>

    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.changeEnemyHp(enemy, -amount)
        val entityId = world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == enemy }
        val cParticle = mParticle.create(entityId)
        cParticle.effect = game.assets.get("assets/binassets/particles/damage_red.p", ParticleEffectDescriptor::class.java)
            .createEffectInstance()
        endAnimation()
    }

}
