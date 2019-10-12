package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.SpellCastedEvent
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.combat.TurnStartEvent
import com.pipai.dragontiles.data.Element
import net.mostlyoriginal.api.event.common.Subscribe

class Invoke(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:Invoke"
    override val requirement: ComponentRequirement = Single()
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = InvokeImpl(this)
}

class InvokeImpl(spell: Spell) : SpellInstance(spell, if (spell.upgraded) 2 else 1) {

    override fun baseDamage(): Int = 2

    override fun onCast(params: CastParams, api: CombatApi) {
        api.attack(params.targets.first(), elemental(components()), baseDamage())
    }
}

class Strike(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:Strike"
    override val requirement: ComponentRequirement = Sequential(3, elementalSet)
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = StrikeImpl(this)
}

class StrikeImpl(spell: Spell) : SpellInstance(spell, 1) {

    override fun baseDamage(): Int = 7

    override fun onCast(params: CastParams, api: CombatApi) {
        api.attack(params.targets.first(), elemental(components()), baseDamage() + if (spell.upgraded) 3 else 0)
    }
}

class RampStrike(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:RampStrike"
    override val requirement: ComponentRequirement = Sequential(3, elementalSet)
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = RampStrikeImpl(this)
}

class RampStrikeImpl(spell: Spell) : SpellInstance(spell, 1) {

    private var spellsCasted = 0

    override fun baseDamage(): Int = 3 + spellsCasted * (if (spell.upgraded) 3 else 2)

    @Subscribe
    fun onTurnStart(ev: TurnStartEvent) {
        spellsCasted = 0
    }

    @Subscribe
    fun onSpellCast(ev: SpellCastedEvent) {
        spellsCasted += 1
    }

    override fun onCast(params: CastParams, api: CombatApi) {
        api.attack(params.targets.first(), elemental(components()), baseDamage())
    }
}

class Break(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:Break"
    override val requirement: ComponentRequirement = Identical(3, elementalSet)
    override val targetType: TargetType = TargetType.SINGLE

    override fun createInstance(): SpellInstance = ConcentrateImpl(this)
}

class BreakImpl(spell: Spell) : SpellInstance(spell, 1) {

    override fun baseDamage(): Int = 0

    override fun onCast(params: CastParams, api: CombatApi) {
        val status = when (elemental(components())) {
            Element.FIRE -> Status.FIRE_BREAK
            Element.ICE -> Status.ICE_BREAK
            Element.LIGHTNING -> Status.LIGHTNING_BREAK
            else -> throw IllegalStateException("Attempted to cast Break with ${components()}")
        }
        api.changeStatusIncrement(status, if (spell.upgraded) 4 else 3)
    }
}

class Concentrate(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:Concentrate"
    override val requirement: ComponentRequirement = Identical(2, arcaneSet)
    override val targetType: TargetType = TargetType.NONE

    override fun createInstance(): SpellInstance = ConcentrateImpl(this)
}

class ConcentrateImpl(spell: Spell) : SpellInstance(spell, 1) {

    override fun baseDamage(): Int = 0

    override fun onCast(params: CastParams, api: CombatApi) {
        api.changeStatusIncrement(Status.POWER, if (spell.upgraded) 3 else 2)
    }
}
