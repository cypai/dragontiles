package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.ParticleEffectComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class ParticleFxAnimation(
    private val position: Vector2,
    private val effect: ParticleEffectDescriptor,
    private val sound: Sound?,
    duration: Float
) : Animation(duration) {

    constructor(position: Vector2, effect: ParticleEffectDescriptor, sound: Sound?) : this(position, effect, sound, -1f)

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mParticle: ComponentMapper<ParticleEffectComponent>

    override fun startAnimation() {
        sound?.play()
        val id = world.create()
        mXy.create(id).setXy(position)
        val cParticle = mParticle.create(id)
        cParticle.endStrategy = EndStrategy.DESTROY
        cParticle.effect = effect.createEffectInstance()
        endAnimation()
    }

}
