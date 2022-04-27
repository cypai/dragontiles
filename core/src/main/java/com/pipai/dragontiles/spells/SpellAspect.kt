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

data class FluxGainAspect(var amount: Int) : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        api.dealFluxDamageToHero(amount, false)
    }
}

data class FluxLossAspect(var amount: Int, val autoImpl: Boolean = true) : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        if (autoImpl) {
            api.heroLoseFlux(amount)
        }
    }
}

data class TempMaxFluxChangeAspect(var amount: Int) : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        api.changeTemporaryMaxFlux(amount)
    }
}

class ExhaustAspect : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        if (spell is StandardSpell) {
            spell.exhausted = true
        }
    }

    override fun adjustDescription(description: String): String {
        return if (description.isEmpty()) {
            "${Keywords.EXHAUST}."
        } else {
            "$description ${Keywords.EXHAUST}."
        }
    }
}

class RepeatableAspect : SpellAspect {
    override fun adjustDescription(description: String): String {
        return if (description.isEmpty()) {
            "${Keywords.REPEATABLE}."
        } else {
            "$description ${Keywords.REPEATABLE}."
        }
    }
}

data class LimitedRepeatableAspect(var max: Int) : SpellAspect {
    override fun adjustDescription(description: String): String {
        return if (description.isEmpty()) {
            "${Keywords.REPEATABLE} !r."
        } else {
            "$description ${Keywords.REPEATABLE} !r."
        }
    }
}

data class SwapAspect(var amount: Int) : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        api.swapQuery(amount)
    }

    override fun adjustDescription(description: String): String {
        return if (description.isEmpty()) {
            "${Keywords.SWAP} !swap."
        } else {
            "$description ${Keywords.SWAP} !swap."
        }
    }
}

data class FetchAspect(var amount: Int?) : SpellAspect {
    override suspend fun onCast(spell: Spell, api: CombatApi) {
        if (amount == null) {
            api.fetch()
        } else {
            api.fetch(amount!!)
        }
    }

    override fun adjustDescription(description: String): String {
        return if (amount == null) {
            if (description.isEmpty()) {
                "${Keywords.FETCH}."
            } else {
                "$description ${Keywords.FETCH}."
            }
        } else {
            if (description.isEmpty()) {
                "${Keywords.FETCH} !fetch."
            } else {
                "$description ${Keywords.FETCH} !fetch."
            }
        }
    }
}

data class StackableAspect(val status: Status, val dynamicId: Int) : SpellAspect

data class XAspect(var amount: Int) : SpellAspect

class TransformAspect : SpellAspect
class NotManuallyDeactivateable : SpellAspect
class PreserveComponentOrder : SpellAspect
class Heatsink : SpellAspect
class Groundwire : SpellAspect
