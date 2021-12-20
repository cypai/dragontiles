package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element

abstract class Relic : DamageAdjustable {
    abstract val strId: String
    abstract val assetName: String

    lateinit var api: CombatApi

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int = 0
    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float = 1f
}
