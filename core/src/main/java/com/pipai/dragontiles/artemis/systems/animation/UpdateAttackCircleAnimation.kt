package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.RadialSpriteComponent
import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class UpdateAttackCircleAnimation(world: World,
                                  private val attack: CountdownAttack) : Animation(world) {

    private lateinit var mRadial: ComponentMapper<RadialSpriteComponent>
    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        val id = world.fetch(allOf(AttackCircleComponent::class))
                .first { mAttackCircle.get(it).id == attack.id }

        val cAttackCircle = mAttackCircle.get(id)
        cAttackCircle.setByCountdown(attack)
        val angularIncrement = 360f / cAttackCircle.maxTurns
        val cRadial = mRadial.get(id)
        cRadial.sprite.setAngle(angularIncrement * cAttackCircle.turnsLeft)

        endAnimation(30)
    }

}
