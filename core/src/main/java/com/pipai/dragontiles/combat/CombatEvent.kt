package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import net.mostlyoriginal.api.event.common.Event

interface CombatEvent : Event

data class DrawEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class HandAdjustedEvent(val hand: List<TileInstance>) : CombatEvent

data class DrawToOpenPoolEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class OpenPoolAdjustedEvent(val openPool: List<TileInstance>) : CombatEvent

data class PlayerAttackEnemyEvent(val target: Enemy, val element: Element, val amount: Int) : CombatEvent

data class EnemyDamageEvent(val target: Enemy, val amount: Int) : CombatEvent

data class EnemyCountdownAttackEvent(val enemy: Enemy, val countdownAttack: CountdownAttack) : CombatEvent

data class CountdownAttackTickEvent(val countdownAttack: CountdownAttack) : CombatEvent

data class CountdownAttackResolveEvent(val countdownAttack: CountdownAttack) : CombatEvent

data class PlayerDamageEvent(val amount: Int) : CombatEvent

data class ComponentConsumeEvent(val components: List<TileInstance>) : CombatEvent

class GameOverEvent : CombatEvent
