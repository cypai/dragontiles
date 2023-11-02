package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.components.ParticleEffectComponent
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.combat.PlayerFluxDamageEvent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.scry
import com.pipai.dragontiles.utils.particleAssetPath
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class PlayerFluxDamageAnimation(private val data: PlayerFluxDamageEvent) : Animation() {
    private lateinit var mHero: ComponentMapper<HeroComponent>
    private lateinit var mParticle: ComponentMapper<ParticleEffectComponent>

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sUi: CombatUiSystem
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        val entityId = world.scry(allOf(HeroComponent::class)).first()
        val cHero = mHero.get(entityId)
        sTop.setFluxRelative(data.amount)
        sCombatantState.changeHeroFlux(data.amount)
        sUi.updateSpellCardFluxReq(cHero.flux, cHero.fluxMax)

        if (data.showParticleAnimation) {
            val cParticle = mParticle.create(entityId)
            cParticle.effect =
                game.assets.get(particleAssetPath("damage_red_flux.p"), ParticleEffectDescriptor::class.java)
                    .createEffectInstance()
        }

        endAnimation()
    }

}
