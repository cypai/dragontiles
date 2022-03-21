package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class DefaultAttackAnimation(val enemy: Enemy) : Animation() {

    override fun startAnimation() {
        endAnimation()
    }
}
