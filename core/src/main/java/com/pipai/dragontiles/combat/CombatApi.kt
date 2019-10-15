package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.getLogger
import kotlin.coroutines.suspendCoroutine

class CombatApi(val combat: Combat,
                val spellInstances: List<SpellInstance>,
                private val eventBus: SuspendableEventBus) {

    private val logger = getLogger()

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    fun castSpell(spellInstance: SpellInstance) {
        eventBus.dispatch(SpellCastedEvent(spellInstance))
    }

    suspend fun draw(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        val drawnDiscardTiles: MutableList<TileInstance> = mutableListOf()
        var currentSize = combat.hand.size
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            if (currentSize >= combat.hero.handSize) {
                drawnDiscardTiles.add(tile)
            } else {
                drawnTiles.add(Pair(tile, currentSize))
                currentSize += 1
            }
        }
        if (drawnTiles.isNotEmpty()) {
            combat.hand.addAll(drawnTiles.map { it.first })
            eventBus.suspendDispatch(DrawEvent(drawnTiles), this)
            eventBus.suspendDispatch(DrawPostEvent(drawnTiles), this)
        }
        if (drawnDiscardTiles.isNotEmpty()) {
            combat.discardPile.addAll(drawnDiscardTiles)
            eventBus.dispatch(DrawDiscardedEvent(drawnDiscardTiles))
        }
    }

    fun transformTile(tileInstance: TileInstance, tile: Tile) {
        val previous = tileInstance.tile
        tileInstance.tile = tile
        eventBus.dispatch(TileTransformedEvent(tileInstance, previous))
    }

    fun drawFromOpenPool(tiles: List<TileInstance>) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        tiles.forEach {
            combat.openPool.remove(it)
            combat.hand.add(it)
            drawnTiles.add(Pair(it, combat.hand.size - 1))
        }
        eventBus.dispatch(DrawFromOpenPoolEvent(drawnTiles))
    }

    fun sortHand() {
        combat.hand.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        eventBus.dispatch(HandAdjustedEvent(combat.hand))
    }

    fun drawToOpenPool(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            combat.openPool.add(tile)
            drawnTiles.add(Pair(tile, combat.openPool.size - 1))
        }
        eventBus.dispatch(DrawToOpenPoolEvent(drawnTiles))
    }

    fun sortOpenPool() {
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

    fun attack(target: Enemy, element: Element, amount: Int) {
        eventBus.dispatch(PlayerAttackEnemyEvent(target, element, amount))
        val damage = calculateTargetDamage(target, element, amount)
        target.hp -= damage
        eventBus.dispatch(EnemyDamageEvent(target, damage))
    }

    fun consume(components: List<TileInstance>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        eventBus.dispatch(ComponentConsumeEvent(components))
        sortHand()
    }

    fun dealDamageToHero(damage: Int) {
        combat.hero.hp -= damage
        eventBus.dispatch(PlayerDamageEvent(damage))
        if (combat.hero.hp <= 0) {
            eventBus.dispatch(GameOverEvent())
        }
    }

    fun enemyAttack(enemy: Enemy, countdownAttack: CountdownAttack) {
        combat.incomingAttacks.add(countdownAttack)
        eventBus.dispatch(EnemyCountdownAttackEvent(enemy, countdownAttack))
    }

    fun updateCountdownAttack(countdownAttack: CountdownAttack) {
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
                eventBus.dispatch(QueryTilesEvent(text, tiles, minAmount, maxAmount, it))
            }
        }
    }

    suspend fun queryOpenPoolDraw() {
        val amount = combat.hero.handSize - combat.hand.size
        val tiles = queryTiles("Select up to $amount tile(s) to draw from the Open Pool",
                combat.openPool, 0, amount)
        if (tiles.isNotEmpty()) {
            drawFromOpenPool(tiles)
        }
    }

}
