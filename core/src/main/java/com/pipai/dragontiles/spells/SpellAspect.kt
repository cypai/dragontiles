package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.status.Status

interface SpellAspect {
    suspend fun onCast(spell: Spell, api: CombatApi) {}

    fun adjustDescription(description: String): String {
        return description
    }
}

data class AttackDamageAspect(var amount: Int) : SpellAspect

data class FluxGainAspect(var amount: Int) : SpellAspect

data class FluxLossAspect(var amount: Int) : SpellAspect

data class TempMaxFluxGainAspect(var amount: Int) : SpellAspect

class PostExhaustAspect : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description ${Keywords.EXHAUST}."
    }
}

class RepeatableAspect : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description ${Keywords.REPEATABLE}."
    }
}

data class LimitedRepeatableAspect(var max: Int) : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description ${Keywords.REPEATABLE} !r."
    }
}

data class SwapAspect(var amount: Int) : SpellAspect {
    override fun adjustDescription(description: String): String {
        return "$description ${Keywords.SWAP} !swap."
    }
}

data class StackableAspect(val status: Status, val dynamicId: Int) : SpellAspect

data class XAspect(val amount: Int) : SpellAspect

class TransformAspect : SpellAspect
class NotManuallyDeactivateable : SpellAspect
class PreserveComponentOrder : SpellAspect
