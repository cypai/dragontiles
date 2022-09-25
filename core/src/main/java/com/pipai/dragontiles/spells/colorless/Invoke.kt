package com.pipai.dragontiles.spells.colorless

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.systems.animation.ParticleFxAnimation
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.pfx
import com.pipai.dragontiles.utils.sound
import com.pipai.dragontiles.utils.withAll

class Invoke : StandardSpell() {
    override val id: String = "base:spells:Invoke"
    override val requirement: ComponentRequirement = Single()
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.STARTER
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(1),
        FluxGainAspect(1),
    )

    override fun flags(): List<CombatFlag> {
        return super.flags().withAll(listOf(CombatFlag.INVOKE))
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.animate(ParticleFxAnimation(Vector2(4f, 4f), api.assets.pfx("damage_red.p"), api.assets.sound("invoke_fire.wav")))
        api.attack(target, elemental(components()), baseDamage(), flags())
    }
}
