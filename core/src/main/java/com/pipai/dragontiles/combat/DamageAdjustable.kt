package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element

interface DamageAdjustable {
    fun queryForAdditionalFlags(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): List<CombatFlag> = listOf()
    fun queryFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int = 0
    fun queryScaledAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Float = 1f
    fun queryPostScaleFlatAdjustment(origin: Combatant?, target: Combatant?, element: Element, flags: List<CombatFlag>): Int = 0
}
