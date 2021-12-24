package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element

interface DamageAdjustable {
    fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int
    fun queryScaledAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Float
}
