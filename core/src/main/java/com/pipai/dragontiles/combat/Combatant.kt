package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.Enemy

sealed class Combatant {
    object HeroCombatant : Combatant()
    data class EnemyCombatant(val enemy: Enemy) : Combatant()
}
