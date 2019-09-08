package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.enemies.Enemy

class Invoke(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:invoke"
    override val requirement: ComponentRequirement = Single()
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = InvokeImpl(this)
}

class InvokeImpl(spell: Spell) : SpellInstance(spell, if (spell.upgraded) 2 else 1) {

    override fun onCast(targets: List<Enemy>, api: CombatApi) {
        api.attack(targets.first(), elemental(components()), 2)
    }
}
