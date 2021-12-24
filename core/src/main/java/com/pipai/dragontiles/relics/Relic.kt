package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
        return RelicInstance(id, 0)
    }

    fun withCounter(counter: Int): Relic {
        this.counter = counter
        return this
    }

    open fun onPickup(api: GlobalApi) {
    }

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int = 0
    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float = 1f
}

data class RelicInstance(override val id: String, var counter: Int) : Localized
