package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Rune
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.StandardSpell
import com.pipai.dragontiles.status.Status
import net.mostlyoriginal.api.event.common.Event
import kotlin.coroutines.Continuation

interface CombatEvent : Event

data class TurnStartEvent(val turnNumber: Int) : CombatEvent

data class TurnEndEvent(val turnNumber: Int) : CombatEvent

data class EnemyTurnStartEvent(val turnNumber: Int) : CombatEvent

data class EnemyTurnEndEvent(val turnNumber: Int) : CombatEvent

data class DrawEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class DrawDiscardedEvent(val tiles: List<TileInstance>) : CombatEvent

data class TileTransformedEvent(val tile: TileInstance, val previous: TileInstance) : CombatEvent

data class TileDestroyedEvent(val tile: TileInstance) : CombatEvent

data class TileStatusChangeEvent(val tiles: List<TileInstance>, val tileStatus: TileStatus) : CombatEvent

data class TilesAddedToHandEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class TilesAddedDiscardedEvent(val tiles: List<TileInstance>) : CombatEvent

data class DrawFromOpenPoolEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class EnemyChangeIntentEvent(val enemy: Enemy, val intent: Intent?) : CombatEvent

data class HandAdjustedEvent(val hand: List<TileInstance>, val assigned: MutableMap<Int, List<TileInstance>>) :
    CombatEvent

data class DrawToOpenPoolEvent(val tiles: List<Pair<TileInstance, Int>>) : CombatEvent

data class OpenPoolToDiscardEvent(val tiles: List<TileInstance>) : CombatEvent

data class OpenDiscardEvent(val tiles: List<TileInstance>) : CombatEvent

data class OpenPoolAdjustedEvent(val openPool: List<TileInstance>) : CombatEvent

data class PlayerAttackEnemyEvent(val target: Enemy, val element: Element, val amount: Int) : CombatEvent

data class EnemyFluxDamageEvent(val enemy: Enemy, val amount: Int) : CombatEvent

data class EnemyLoseFluxEvent(val enemy: Enemy, val amount: Int) : CombatEvent

data class EnemyDamageEvent(val enemy: Enemy, val amount: Int) : CombatEvent

data class EnemyDefeatedEvent(val enemy: Enemy) : CombatEvent

class BattleWinEvent : CombatEvent

data class PlayerFluxDamageEvent(val amount: Int) : CombatEvent

data class PlayerLoseFluxEvent(val amount: Int) : CombatEvent

data class PlayerHealEvent(val amount: Int) : CombatEvent

data class PlayerDamageEvent(val amount: Int) : CombatEvent

data class ComponentConsumeEvent(val components: List<TileInstance>) : CombatEvent

data class SpellCastedEvent(val spell: Spell) : CombatEvent

data class RuneActivatedEvent(val rune: Rune) : CombatEvent

data class RuneDeactivatedEvent(val rune: Rune) : CombatEvent

data class SpellExhaustedEvent(val spell: StandardSpell) : CombatEvent

class GameOverEvent : CombatEvent

data class QueryTilesEvent(
    val text: String,
    val tiles: List<TileInstance>,
    val minAmount: Int,
    val maxAmount: Int,
    val continuation: Continuation<List<TileInstance>>
) : CombatEvent

data class QueryTileOptionsEvent(
    val text: String,
    val displayTile: TileInstance?,
    val options: List<Tile>,
    val minAmount: Int,
    val maxAmount: Int,
    val continuation: Continuation<List<Tile>>
) : CombatEvent

data class EnemyStatusChangeEvent(
    val enemy: Enemy,
    val status: Status,
    val previousAmount: Int,
) : CombatEvent

data class StatusOverviewAdjustedEvent(
    val heroStatus: List<Status>,
    val enemyStatus: Map<Int, List<Status>>
) : CombatEvent

data class QuerySwapEvent(val amount: Int) : CombatEvent
data class SwapData(val spellInHand: List<Spell>, val spellOnSide: List<Spell>)

data class SwapEvent(val spellInHand: List<Spell>, val spellOnSide: List<Spell>) : CombatEvent
