package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile

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
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
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
    override val requirement: ComponentRequirement = Sequential(3, SuitGroup.ELEMENTAL)
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
    override val requirement: ComponentRequirement = Identical(2, SuitGroup.ARCANE)
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): Concentrate {
        return Concentrate(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeStatusIncrement(Status.STRENGTH, if (upgraded) 3 else 2)
    }
}

class FeedbackLoop(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:FeedbackLoop"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ARCANE)
    override val targetType: TargetType = TargetType.NONE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = 0

    override fun newClone(upgraded: Boolean): FeedbackLoop {
        return FeedbackLoop(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.changeStatusIncrement(Status.STRENGTH, api.combat.heroStatus[Status.STRENGTH])
    }
}

class Blast(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Blast"
    override val requirement: ComponentRequirement = Identical(3)
    override val targetType: TargetType = TargetType.AOE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int = if (upgraded) 6 else 5

    override fun newClone(upgraded: Boolean): Blast {
        return Blast(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        params.targets.forEach {
            api.attack(api.getTargetable(it), elemental(components()), baseDamage())
        }
    }
}

class Explosion(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Explosion"
    override val requirement: ComponentRequirement = Identical(3)
    override val targetType: TargetType = TargetType.AOE

    override var repeatableMax: Int = 1

    override fun baseDamage(): Int {
        return if (upgraded) {
            3 * numeric(components())
        } else {
            2 * numeric(components())
        }
    }

    override fun newClone(upgraded: Boolean): Explosion {
        return Explosion(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        exhausted = true
        repeat(2) {
            params.targets.forEach {
                api.attack(api.getTargetable(it), elemental(components()), baseDamage())
            }
        }
    }
}

class Spark(upgraded: Boolean) : Spell(upgraded) {
    override val id: String = "base:spells:Spark"
    override val requirement: ComponentRequirement = SinglePredicate(
            { it.tile.let { t -> t is Tile.ElementalTile && t.number == 1 } },
            SuitGroup.ELEMENTAL)
    override val targetType: TargetType = TargetType.SINGLE

    override var repeatableMax: Int = Int.MAX_VALUE

    override fun baseDamage(): Int {
        return if (upgraded) {
            4
        } else {
            2
        }
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getTargetable(params.targets.first())
        api.attack(target, elemental(components()), baseDamage())
    }

    override fun newClone(upgraded: Boolean): Invoke {
        return Invoke(upgraded)
    }
}
