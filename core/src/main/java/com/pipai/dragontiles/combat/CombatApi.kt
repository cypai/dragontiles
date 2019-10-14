package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.getLogger
import net.mostlyoriginal.api.event.common.EventSystem
import kotlin.coroutines.suspendCoroutine

class CombatApi(val combat: Combat,
                val spellInstances: List<SpellInstance>,
                private val eventSystem: EventSystem) {

    private val logger = getLogger()

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    fun castSpell(spellInstance: SpellInstance) {
        eventSystem.dispatch(SpellCastedEvent(spellInstance))
    }

    fun draw(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            if (combat.hand.size >= combat.hero.handSize) {
                combat.discardPile.add(tile)
            } else {
                combat.hand.add(tile)
                drawnTiles.add(Pair(tile, combat.hand.size))
            }
        }
        eventSystem.dispatch(DrawEvent(drawnTiles))
    }

    fun drawFromOpenPool(tiles: List<TileInstance>) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        tiles.forEach {
            combat.openPool.remove(it)
            combat.hand.add(it)
            drawnTiles.add(Pair(it, combat.hand.size))
        }
        eventSystem.dispatch(DrawFromOpenPoolEvent(drawnTiles))
    }

    fun sortHand() {
        combat.hand.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        eventSystem.dispatch(HandAdjustedEvent(combat.hand))
    }

    fun drawToOpenPool(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            combat.openPool.add(tile)
            drawnTiles.add(Pair(tile, combat.openPool.size))
        }
        eventSystem.dispatch(DrawToOpenPoolEvent(drawnTiles))
    }

    fun sortOpenPool() {
        combat.openPool.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        eventSystem.dispatch(OpenPoolAdjustedEvent(combat.openPool))
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
        eventSystem.dispatch(PlayerAttackEnemyEvent(target, element, amount))
        val damage = calculateTargetDamage(target, element, amount)
        target.hp -= damage
        eventSystem.dispatch(EnemyDamageEvent(target, damage))
    }

    fun consume(components: List<TileInstance>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        eventSystem.dispatch(ComponentConsumeEvent(components))
        sortHand()
    }

    fun dealDamageToHero(damage: Int) {
        combat.hero.hp -= damage
        eventSystem.dispatch(PlayerDamageEvent(damage))
        if (combat.hero.hp <= 0) {
            eventSystem.dispatch(GameOverEvent())
        }
    }

    fun enemyAttack(enemy: Enemy, countdownAttack: CountdownAttack) {
        combat.incomingAttacks.add(countdownAttack)
        eventSystem.dispatch(EnemyCountdownAttackEvent(enemy, countdownAttack))
    }

    fun updateCountdownAttack(countdownAttack: CountdownAttack) {
        countdownAttack.turnsLeft -= 1
        if (countdownAttack.turnsLeft == 0) {
            combat.incomingAttacks.remove(countdownAttack)
            eventSystem.dispatch(CountdownAttackResolveEvent(countdownAttack))
            val damage = countdownAttack.baseDamage * countdownAttack.multiplier
            dealDamageToHero(damage)
        } else {
            eventSystem.dispatch(CountdownAttackTickEvent(countdownAttack))
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
        return suspendCoroutine {
            eventSystem.dispatch(QueryTilesEvent(text, tiles, minAmount, maxAmount, it))
        }
    }

    suspend fun queryOpenPoolDraw() {
        val tiles = queryTiles("Select tiles to draw from the Open Pool", combat.openPool, 0, Int.MAX_VALUE)
        drawFromOpenPool(tiles)
    }

}
