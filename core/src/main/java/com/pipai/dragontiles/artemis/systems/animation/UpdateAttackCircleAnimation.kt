package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.TextLabelComponent
import com.pipai.dragontiles.combat.CountdownAttack
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class UpdateAttackCircleAnimation(private val attack: CountdownAttack) : Animation() {

    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
    private lateinit var mTextLabel: ComponentMapper<TextLabelComponent>

    override fun startAnimation() {
        val id = world.fetch(allOf(AttackCircleComponent::class))
                .first { mAttackCircle.get(it).id == attack.id }

        val cAttackCircle = mAttackCircle.get(id)
        cAttackCircle.setByCountdown(attack)

        cAttackCircle.swordId?.let {
            mTextLabel.get(it).text = attack.calcAttackPower().toString()
        }
        cAttackCircle.spiralId?.let {
            mTextLabel.get(it).text = attack.calcEffectPower().toString()
        }
        mTextLabel.get(cAttackCircle.hourglassId).text = attack.turnsLeft.toString()

        if (attack.isActive()) {
            endAnimation(0.5f)
        } else {
            endAnimation()
        }
    }

}
