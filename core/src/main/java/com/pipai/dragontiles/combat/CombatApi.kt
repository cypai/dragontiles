package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Spell
import kotlin.coroutines.suspendCoroutine

class CombatApi(val runData: RunData,
                val combat: Combat,
                val spells: List<Spell>,
                private val eventBus: SuspendableEventBus) {

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    suspend fun castSpell(spell: Spell) {
        eventBus.dispatch(SpellCastedEvent(spell))
    }

    suspend fun draw(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        val drawnDiscardTiles: MutableList<TileInstance> = mutableListOf()
        var currentSize = combat.hand.size
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            if (currentSize >= runData.hero.handSize) {
                drawnDiscardTiles.add(tile)
            } else {
                drawnTiles.add(Pair(tile, currentSize))
                currentSize += 1
            }
        }
        if (drawnTiles.isNotEmpty()) {
            combat.hand.addAll(drawnTiles.map { it.first })
            eventBus.dispatch(DrawEvent(drawnTiles))
        }
        if (drawnDiscardTiles.isNotEmpty()) {
            combat.discardPile.addAll(drawnDiscardTiles)
            eventBus.dispatch(DrawDiscardedEvent(drawnDiscardTiles))
        }
    }

    suspend fun transformTile(tileInstance: TileInstance, tile: Tile) {
        val previous = tileInstance.tile
        tileInstance.tile = tile
        eventBus.dispatch(TileTransformedEvent(tileInstance, previous))
        sortHand()
    }

    suspend fun drawFromOpenPool(tiles: List<TileInstance>) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        tiles.forEach {
            combat.openPool.remove(it)
            combat.hand.add(it)
            drawnTiles.add(Pair(it, combat.hand.size - 1))
        }
        eventBus.dispatch(DrawFromOpenPoolEvent(drawnTiles))
    }

    suspend fun sortHand() {
        combat.hand.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        eventBus.dispatch(HandAdjustedEvent(combat.hand))
    }

    suspend fun drawToOpenPool(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            combat.openPool.add(tile)
            drawnTiles.add(Pair(tile, combat.openPool.size - 1))
        }
        eventBus.dispatch(DrawToOpenPoolEvent(drawnTiles))
    }

    suspend fun sortOpenPool() {
        combat.openPool.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        eventBus.dispatch(OpenPoolAdjustedEvent(combat.openPool))
    }

    fun calculateBaseDamage(amount: Int): Int {
        return amount + (combat.heroStatus[Status.POWER] ?: 0)
    }

    fun calculateTargetDamage(target: Enemy, element: Element, amount: Int): Int {
        var damage = calculateBaseDamage(amount)
        val broken = when (element) {
            Element.FIRE -> combat.enemyStatus[target.id]!![Status.FIRE_BREAK] != null
            Element.ICE -> combat.enemyStatus[target.id]!![Status.ICE_BREAK] != null
            Element.LIGHTNING -> combat.enemyStatus[target.id]!![Status.LIGHTNING_BREAK] != null
            else -> false
        }
        if (broken) {
            damage *= 2
        }
        return damage
    }

    suspend fun attack(target: Enemy, element: Element, amount: Int) {
        eventBus.dispatch(PlayerAttackEnemyEvent(target, element, amount))
        val damage = calculateTargetDamage(target, element, amount)
        dealDamageToEnemy(target, damage)
    }

    suspend fun dealDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.hp -= damage
        eventBus.dispatch(EnemyDamageEvent(enemy, damage))
        if (enemy.hp <= 0) {
            eventBus.dispatch(EnemyDefeatedEvent(enemy))
            if (combat.enemies.all { it.hp <= 0 }) {
                combat.heroStatus.clear()
                eventBus.dispatch(BattleWinEvent())
            }
        }
    }

    suspend fun consume(components: List<TileInstance>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        eventBus.dispatch(ComponentConsumeEvent(components))
        sortHand()
    }

    suspend fun dealDamageToHero(damage: Int) {
        runData.hero.hp -= damage
        eventBus.dispatch(PlayerDamageEvent(damage))
        if (runData.hero.hp <= 0) {
            eventBus.dispatch(GameOverEvent())
        }
    }

    suspend fun enemyAttack(enemy: Enemy, countdownAttack: CountdownAttack) {
        combat.incomingAttacks.add(countdownAttack)
        eventBus.dispatch(EnemyCountdownAttackEvent(enemy, countdownAttack))
    }

    suspend fun updateCountdownAttack(countdownAttack: CountdownAttack) {
        countdownAttack.turnsLeft -= 1
        if (countdownAttack.turnsLeft == 0) {
            combat.incomingAttacks.remove(countdownAttack)
            eventBus.dispatch(CountdownAttackResolveEvent(countdownAttack))
            val damage = countdownAttack.baseDamage * countdownAttack.multiplier
            dealDamageToHero(damage)
        } else {
            eventBus.dispatch(CountdownAttackTickEvent(countdownAttack))
        }
    }

    fun changeStatus(status: Status, amount: Int) {
        combat.heroStatus[status] = amount
    }

    fun changeStatusIncrement(status: Status, increment: Int) {
        changeStatus(status, (combat.heroStatus[status] ?: 0) + increment)
    }

    fun changeEnemyStatus(id: Int, status: Status, amount: Int) {
        combat.enemyStatus[id]!![status] = amount
    }

    fun changeEnemyStatusIncrement(id: Int, status: Status, increment: Int) {
        changeEnemyStatus(id, status, (combat.enemyStatus[id]!![status] ?: 0) + increment)
    }

    suspend fun queryTiles(text: String, tiles: List<TileInstance>, minAmount: Int, maxAmount: Int): List<TileInstance> {
        return if (maxAmount == 0 || maxAmount < minAmount) {
            listOf()
        } else {
            suspendCoroutine {
                eventBus.syncDispatch(QueryTilesEvent(text, tiles, minAmount, maxAmount, it))
            }
        }
    }

    suspend fun queryOpenPoolDraw() {
        val amount = runData.hero.handSize - combat.hand.size
        val tiles = queryTiles("Select up to $amount tile(s) to draw from the Open Pool",
                combat.openPool, 0, amount)
        if (tiles.isNotEmpty()) {
            drawFromOpenPool(tiles)
        }
    }

    suspend fun queryTileOptions(text: String,
                                 displayTile: TileInstance?,
                                 options: List<Tile>,
                                 minAmount: Int,
                                 maxAmount: Int): List<Tile> {

        return if (maxAmount == 0 || maxAmount < minAmount) {
            listOf()
        } else {
            suspendCoroutine {
                eventBus.syncDispatch(QueryTileOptionsEvent(text, displayTile, options, minAmount, maxAmount, it))
            }
        }
    }

    suspend fun queryTransform(tile: TileInstance, options: List<Tile>): Tile {
        return queryTileOptions("Transform this tile", tile, options, 1, 1).first()
    }

}
