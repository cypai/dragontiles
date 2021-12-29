package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlin.reflect.full.createInstance

abstract class Relic : Localized, DamageAdjustable {
    abstract val assetName: String
    abstract val rarity: Rarity
    abstract val showCounter: Boolean
    var counter = 0

    fun newClone(): Relic {
        return this::class.createInstance()
    }

    fun toInstance(): RelicInstance {
        return RelicInstance(id, counter)
    }

    fun withCounter(counter: Int): Relic {
        this.counter = counter
        return this
    }

    open fun onPickup(api: GlobalApi) {
    }

    override fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int = 0
    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float = 1f
}

data class RelicInstance(override val id: String, var counter: Int) : Localized
