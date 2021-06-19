package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class PlayerDamageAnimation(private val damage: Int) : Animation() {
    private lateinit var mHero: ComponentMapper<HeroComponent>

    private lateinit var sTop: TopRowUiSystem

    override fun startAnimation() {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        cHero.hp -= damage
        if (cHero.hp < 0) {
            cHero.hp = 0
        }
        sTop.setHpRelative(-damage)
        endAnimation()
    }

}
