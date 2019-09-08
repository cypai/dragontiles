package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.enemies.Enemy

class Invoke : Spell {
    override val name: String = "Invoke"
    override val description: String = "Cast 2 @Elemental."
    override val requirement: ComponentRequirement = Single()
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = InvokeImpl(this)
}

class InvokeImpl(spell: Spell) : SpellInstance(spell, 1) {

    override fun onCast(targets: List<Enemy>, api: CombatApi) {
        api.attack(targets.first(), elemental(components()), 2)
    }
}
