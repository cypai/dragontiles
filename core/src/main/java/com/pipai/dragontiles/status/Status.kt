package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element

abstract class Status(var amount: Int) : DamageAdjustable {
    abstract val strId: String
    abstract val displayAmount: Boolean

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int = 0
    override fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float = 1f
}
