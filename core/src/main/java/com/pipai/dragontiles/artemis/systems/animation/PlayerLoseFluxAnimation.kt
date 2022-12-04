package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.scry

class PlayerLoseFluxAnimation(private val amount: Int) : Animation() {
    private lateinit var mHero: ComponentMapper<HeroComponent>

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sUi: CombatUiSystem
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        val cHero = mHero.get(world.scry(allOf(HeroComponent::class)).first())
        sTop.setFluxRelative(-amount)
        sCombatantState.changeHeroFlux(-amount)
        sUi.updateSpellCardFluxReq(cHero.flux, cHero.fluxMax)
        endAnimation()
    }

}
