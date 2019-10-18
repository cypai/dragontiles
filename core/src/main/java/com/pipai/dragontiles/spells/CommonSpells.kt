package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Invoke(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Invoke"
    override val requirement: ComponentRequirement = Single()
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = if (upgraded) 2 else 1

    override fun baseDamage(): Int = 2

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }

    override fun newClone(upgraded: Boolean): Invoke {
        return Invoke(upgraded)
    }
}

class Strike(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Strike"
    override val requirement: ComponentRequirement = Sequential(3, elementalSet)
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 7

    override fun newClone(upgraded: Boolean): Strike {
        return Strike(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage() + if (upgraded) 3 else 0)
    }
}

class RampStrike(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:RampStrike"
    override val requirement: ComponentRequirement = Sequential(3, elementalSet)
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = 1

    private var spellsCasted = 0

    override fun baseDamage(): Int = 3 + spellsCasted * (if (upgraded) 3 else 2)

    override fun newClone(upgraded: Boolean): RampStrike {
        return RampStrike(upgraded)
    }

    @CombatSubscribe
    fun onTurnStart(ev: TurnStartEvent, api: CombatApi) {
        spellsCasted = 0
    }

    @CombatSubscribe
    fun onSpellCast(ev: SpellCastedEvent, api: CombatApi) {
        spellsCasted += 1
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }
}

class Break(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Break"
    override val requirement: ComponentRequirement = Identical(3)
    override val targetType: TargetType = TargetType.SINGLE_ENEMY

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Break {
        return Break(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val status = when (elemental(components())) {
            Element.FIRE -> Status.FIRE_BREAK
            Element.ICE -> Status.ICE_BREAK
            Element.LIGHTNING -> Status.LIGHTNING_BREAK
            Element.NONE -> Status.NONELEMENTAL_BREAK
        }
        api.changeEnemyStatusIncrement(params.targets.first(), status, if (upgraded) 4 else 3)
    }
}

class Concentrate(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Concentrate"
    override val requirement: ComponentRequirement = Identical(2, arcaneSet)
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Concentrate {
        return Concentrate(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeStatusIncrement(Status.POWER, if (upgraded) 3 else 2)
    }
}
