package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.components.ParticleEffectComponent
import com.pipai.dragontiles.artemis.systems.ui.CombatantStateSystem
import com.pipai.dragontiles.artemis.systems.ui.TopRowUiSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.particleAssetPath
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class PlayerDamageAnimation(private val damage: Int) : Animation() {
    private lateinit var mParticle: ComponentMapper<ParticleEffectComponent>

    private lateinit var sTop: TopRowUiSystem
    private lateinit var sCombatantState: CombatantStateSystem

    override fun startAnimation() {
        sCombatantState.changeHeroHp(-damage)
        sTop.setHpRelative(-damage)

        val entityId = world.fetch(allOf(HeroComponent::class)).first()
        val cParticle = mParticle.create(entityId)
        cParticle.effect = game.assets.get(particleAssetPath("damage_red_flux.p"), ParticleEffectDescriptor::class.java)
            .createEffectInstance()

        endAnimation()
    }

}
