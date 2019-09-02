package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile

interface Spell {
    val name: String
    val description: String

    fun createInstance(): SpellInstance
}

abstract class SpellInstance(
        val spell: Spell,
        var repeatable: Boolean,
        var repeatableMax: Int) {

    var repeated = 0
    var exhausted = false

    open fun onCast(components: List<Tile>, api: CombatApi) {
    }

    open fun handleComponents(components: List<Tile>, api: CombatApi) {
        api.consume(components)
    }

    open fun onTurnStart(api: CombatApi) {
    }
}
