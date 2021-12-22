package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class PlayerHealAnimation(private val amount: Int) : Animation() {
    private lateinit var mHero: ComponentMapper<HeroComponent>

    private lateinit var sTop: TopRowUiSystem

    override fun startAnimation() {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        cHero.hp += amount
        if (cHero.hp > cHero.hpMax) {
            cHero.hp = cHero.hpMax
        }
        sTop.setHp(cHero.hp, cHero.hpMax)
        endAnimation()
    }

}
