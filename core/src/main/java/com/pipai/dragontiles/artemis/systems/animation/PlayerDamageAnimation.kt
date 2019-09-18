package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem

class PlayerDamageAnimation(world: World,
                            private val damage: Int,
                            private val hp: Int,
                            private val hpMax: Int) : Animation(world) {

    private lateinit var sUi: CombatUiSystem

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        sUi.setHp(hp, hpMax)
        endAnimation()
    }

}
