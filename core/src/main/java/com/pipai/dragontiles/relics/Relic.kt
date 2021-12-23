package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import kotlinx.serialization.Serializable

@Serializable
abstract class Relic : DamageAdjustable {
    abstract val strId: String
    abstract val assetName: String
    abstract val rarity: Rarity

    open fun onPickup(api: GlobalApi) {
    }

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int = 0
    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float = 1f
}
