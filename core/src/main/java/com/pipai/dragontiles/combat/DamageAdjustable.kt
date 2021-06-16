package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element

interface DamageAdjustable {
    fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int
    fun queryScaledAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Float
}
