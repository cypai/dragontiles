package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.status.Status

interface SpellAspect {
    suspend fun onCast(spell: Spell, api: CombatApi) {}

    fun adjustDescription(description: String): String {
        return description
    }
}

data class AttackDamageAspect(var amount: Int) : SpellAspect

class PostExhaustAspect : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description Exhaust."
    }
}

class RepeatableAspect : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description Repeatable."
    }
}

data class LimitedRepeatableAspect(var max: Int) : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description Repeatable !r."
    }
}

data class StackableAspect(val status: Status, val dynamicId: Int) : SpellAspect
