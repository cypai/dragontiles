package com.pipai.dragontiles.spells

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Tile

class Invoke : Spell {
    override val name: String = "Invoke"
    override val description: String = "Cast 2 @Elemental."

    override fun createInstance(): SpellInstance = InvokeImpl()
}

class InvokeImpl : SpellInstance(Invoke(), false, 2) {

    override fun onCast(components: List<Tile>, api: CombatApi) {
        api.attack(elemental(components), 2)
    }
}
