package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Rune
import com.pipai.dragontiles.spells.StandardSpell
import net.mostlyoriginal.api.event.common.Event
import kotlin.coroutines.Continuation

interface CombatEvent : Event

data class TurnStartEvent(val turnNumber: Int) : CombatEvent

data class TurnEndEvent(val turnNumber: Int) : CombatEvent

data class DrawEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class DrawDiscardedEvent(val tiles: List<TileInstance>) : CombatEvent

data class TileTransformedEvent(val tile: TileInstance, val previous: Tile) : CombatEvent

data class TileDestroyedEvent(val tile: TileInstance) : CombatEvent

data class TilesAddedToHandEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class TilesAddedDiscardedEvent(val tiles: List<TileInstance>) : CombatEvent

data class DrawFromOpenPoolEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class EnemyDiscardEvent(val enemyId: Int, val tile: TileInstance, val location: Int) : CombatEvent

data class HandAdjustedEvent(val hand: List<TileInstance>, val assigned: MutableMap<Int, List<TileInstance>>) : CombatEvent

data class DrawToOpenPoolEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class OpenPoolAdjustedEvent(val openPool: List<TileInstance>) : CombatEvent

data class PlayerAttackEnemyEvent(val target: Enemy, val element: Element, val amount: Int) : CombatEvent

data class PlayerAttackCountdownAttackEvent(val target: CountdownAttack, val element: Element, val amount: Int) : CombatEvent

data class EnemyDamageEvent(val target: Enemy, val amount: Int) : CombatEvent

data class EnemyDefeatedEvent(val enemy: Enemy) : CombatEvent

class BattleWinEvent : CombatEvent

data class EnemyCountdownAttackEvent(val enemy: Enemy, val countdownAttack: CountdownAttack) : CombatEvent

data class CountdownAttackTickEvent(val countdownAttack: CountdownAttack) : CombatEvent

data class CountdownAttackResolveEvent(val countdownAttack: CountdownAttack) : CombatEvent

data class CountdownAttackDamageEvent(val countdownAttack: CountdownAttack, val attackPowerDamage: Int, val effectPowerDamage: Int) : CombatEvent

data class CountdownAttackDisruptedEvent(val countdownAttack: CountdownAttack) : CombatEvent

data class PlayerDamageEvent(val amount: Int) : CombatEvent

data class ComponentConsumeEvent(val components: List<TileInstance>) : CombatEvent

data class SpellCastedEvent(val spell: StandardSpell) : CombatEvent

data class RuneActivatedEvent(val rune: Rune) : CombatEvent

data class RuneDeactivatedEvent(val rune: Rune) : CombatEvent

class GameOverEvent : CombatEvent

data class QueryTilesEvent(val text: String,
                           val tiles: List<TileInstance>,
                           val minAmount: Int,
                           val maxAmount: Int,
                           val continuation: Continuation<List<TileInstance>>) : CombatEvent

data class QueryTileOptionsEvent(val text: String,
                                 val displayTile: TileInstance?,
                                 val options: List<Tile>,
                                 val minAmount: Int,
                                 val maxAmount: Int,
                                 val continuation: Continuation<List<Tile>>) : CombatEvent
