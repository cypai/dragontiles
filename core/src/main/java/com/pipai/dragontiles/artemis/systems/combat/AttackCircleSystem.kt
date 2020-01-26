package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.RadialSpriteComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class AttackCircleSystem : IteratingSystem(allOf()) {

    private val mRadial by require<RadialSpriteComponent>()
    private val mAttackCircle by require<AttackCircleComponent>()

    private val increment = 1f / 60f

    override fun process(entityId: Int) {
        val cAttackCircle = mAttackCircle.get(entityId)
        if (cAttackCircle.up) {
            cAttackCircle.t += increment
        } else {
            cAttackCircle.t -= increment
        }
        if (cAttackCircle.t >= 1f) {
            cAttackCircle.up = false
        } else if (cAttackCircle.t <= 0f) {
            cAttackCircle.up = true
        }
        val cRadial = mRadial.get(entityId)
        val color = cAttackCircle.color
        cRadial.sprite.setColor(Color(color.r, color.g, color.b, cAttackCircle.t))
    }

}
