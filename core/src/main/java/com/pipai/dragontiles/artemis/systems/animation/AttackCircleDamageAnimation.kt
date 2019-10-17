package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.combat.CountdownAttack
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class AttackCircleDamageAnimation(private val attack: CountdownAttack) : Animation() {

    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>

    override fun startAnimation() {
        val id = world.fetch(allOf(AttackCircleComponent::class))
                .first { mAttackCircle.get(it).id == attack.id }

        val cAttackCircle = mAttackCircle.get(id)
        cAttackCircle.setByCountdown(attack)

        endAnimation(30)
    }

}
