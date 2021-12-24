package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class PlayerLoseFluxAnimation(private val amount: Int) : Animation() {
    private lateinit var mHero: ComponentMapper<HeroComponent>

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sUi: CombatUiSystem

    override fun startAnimation() {
        val cHero = mHero.get(world.fetch(allOf(HeroComponent::class)).first())
        cHero.flux -= amount
        if (cHero.flux < 0) {
            cHero.flux = 0
        }
        sTop.setFluxRelative(-amount)
        sUi.updateSpellCardFluxReq(cHero.flux, cHero.fluxMax)
        endAnimation()
    }

}
