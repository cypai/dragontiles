package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.audio.Sound
import com.pipai.dragontiles.artemis.components.ParticleEffectComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.utils.particleAssetPath
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class ParticleFxAnimation(private val sound: Sound?, duration: Float) : Animation(duration) {
    constructor(sound: Sound?) : this(sound, -1f)

    private lateinit var mParticle: ComponentMapper<ParticleEffectComponent>


    override fun startAnimation() {
        sound?.play()
        val id = world.create()
        val cParticle = mParticle.create(id)
        cParticle.effect =
            game.assets.get(particleAssetPath("damage_red_flux.p"), ParticleEffectDescriptor::class.java)
                .createEffectInstance()
        endAnimation()
    }

}
