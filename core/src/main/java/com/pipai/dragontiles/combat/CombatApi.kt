package com.pipai.dragontiles.combat

import com.badlogic.gdx.math.MathUtils.ceil
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Rune
import com.pipai.dragontiles.spells.StandardSpell
import kotlin.coroutines.suspendCoroutine

class CombatApi(val runData: RunData,
                val combat: Combat,
                private val eventBus: SuspendableEventBus) {

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    fun getTargetable(id: Int): Targetable {
        val target: Targetable? = combat.enemies.find { it.id == id }
                ?: combat.enemyAttacks.values.find { it.id == id }
        return target!!
    }

    fun getEnemy(id: Int): Enemy {
        return combat.enemies.first { it.id == id }
    }

    fun getCountdownAttack(id: Int): CountdownAttack {
        return combat.enemyAttacks.values.first { it.id == id }
    }

    suspend fun castSpell(spell: StandardSpell) {
        eventBus.dispatch(SpellCastedEvent(spell))
    }

    suspend fun activateRune(rune: Rune, components: List<TileInstance>) {
        eventBus.dispatch(RuneActivatedEvent(rune))
        val runeIndex = combat.spells.indexOf(rune)
        combat.assigned[runeIndex] = components
        combat.hand.removeAll(components)
    }

    suspend fun deactivateRune(rune: Rune) {
        eventBus.dispatch(RuneDeactivatedEvent(rune))
        val runeIndex = combat.spells.indexOf(rune)
        val components = combat.assigned.remove(runeIndex)!!
        combat.hand.addAll(components)
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
        eventBus.dispatch(HandAdjustedEvent(combat.hand.toList(), combat.assigned))
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
        eventBus.dispatch(OpenPoolAdjustedEvent(combat.openPool.toList()))
    }

    fun calculateBaseDamage(attackerStatus: StatusData, amount: Int): Int {
        return (amount + attackerStatus[Status.STRENGTH]).coerceAtLeast(0)
    }

    fun calculateActualDamage(damageOrigin: DamageOrigin,
                              damageTarget: DamageTarget,
                              attackerStatus: StatusData,
                              targetStatus: StatusData,
                              element: Element,
                              amount: Int): Int {

        var damage = calculateBaseDamage(attackerStatus, amount) - targetStatus[Status.DEFENSE]
        combat.spells.forEach {
            if (it is Rune) {
                damage += it.attackDamageModifier(damageOrigin, damageTarget, attackerStatus, targetStatus, element, amount)
            }
        }
        val broken = when (element) {
            Element.FIRE -> targetStatus.has(Status.FIRE_BREAK)
            Element.ICE -> targetStatus.has(Status.ICE_BREAK)
            Element.LIGHTNING -> targetStatus.has(Status.LIGHTNING_BREAK)
            Element.NONE -> targetStatus.has(Status.NONELEMENTAL_BREAK)
        }
        if (broken) {
            damage = ceil(1.5f * damage.toFloat())
        }
        return damage.coerceAtLeast(0)
    }

    fun calculateAttackDamage(target: Targetable, element: Element, amount: Int): Int {
        return when (target) {
            is Enemy -> calculateActualDamage(
                    DamageOrigin.HERO_ATTACK,
                    DamageTarget.ENEMY,
                    combat.heroStatus,
                    combat.enemyStatus[target.id]!!,
                    element,
                    amount)
            is CountdownAttack -> calculateActualDamage(
                    DamageOrigin.HERO_ATTACK,
                    DamageTarget.ENEMY_SPELL,
                    combat.heroStatus,
                    StatusData(),
                    element,
                    amount)
            else -> throw IllegalStateException("Received unknown target $target")
        }
    }

    suspend fun attack(target: Targetable, element: Element, amount: Int) {
        val damage = calculateAttackDamage(target, element, amount)
        when (target) {
            is Enemy -> {
                eventBus.dispatch(PlayerAttackEnemyEvent(target, element, amount))
                dealDamageToEnemy(target, damage)
            }
            is CountdownAttack -> {
                eventBus.dispatch(PlayerAttackCountdownAttackEvent(target, element, amount))
                dealDamageToCountdownAttack(target, damage)
            }
        }
    }

    suspend fun dealDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.hp -= damage
        eventBus.dispatch(EnemyDamageEvent(enemy, damage))
        if (enemy.hp <= 0) {
            eventBus.dispatch(EnemyDefeatedEvent(enemy))
            disruptEnemyCountdownAttack(enemy.id)
            if (combat.enemies.all { it.hp <= 0 }) {
                combat.heroStatus.clear()
                eventBus.dispatch(BattleWinEvent())
            }
        }
    }

    suspend fun dealDamageToCountdownAttack(ca: CountdownAttack, damage: Int) {
        ca.counteredAttackPower += damage
        if (ca.counteredAttackPower >= ca.attackPower) {
            val residual = ca.counteredAttackPower - ca.attackPower
            ca.counteredAttackPower = ca.attackPower
            ca.counteredEffectPower += residual
            eventBus.dispatch(CountdownAttackDamageEvent(ca, damage - residual, residual))
            if (ca.counteredEffectPower >= ca.effectPower) {
                disruptCountdownAttack(ca)
            }
        } else {
            eventBus.dispatch(CountdownAttackDamageEvent(ca, damage, 0))
        }
    }

    suspend fun disruptCountdownAttack(ca: CountdownAttack) {
        disruptEnemyCountdownAttack(combat.enemyAttacks.entries.find { it.value == ca }!!.key)
    }

    suspend fun disruptEnemyCountdownAttack(enemyId: Int) {
        val ca = combat.enemyAttacks.remove(enemyId)
        ca?.let {
            eventBus.dispatch(CountdownAttackDisruptedEvent(ca))
        }
    }

    suspend fun consume(components: List<TileInstance>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        eventBus.dispatch(ComponentConsumeEvent(components))
        sortHand()
    }

    suspend fun assign(components: List<TileInstance>, rune: Rune) {
        combat.hand.removeAll(components)
        val runeIndex = combat.spells.indexOf(rune)
        combat.assigned[runeIndex]
        sortHand()
    }

    suspend fun dealDamageToHero(damage: Int) {
        runData.hero.hp -= damage
        eventBus.dispatch(PlayerDamageEvent(damage))
        if (runData.hero.hp <= 0) {
            eventBus.dispatch(GameOverEvent())
        }
    }

    fun fetchAttack(enemyId: Int): CountdownAttack? {
        return combat.enemyAttacks[enemyId]
    }

    suspend fun enemyCreateAttack(enemy: Enemy, countdownAttack: CountdownAttack) {
        combat.enemyAttacks[enemy.id] = countdownAttack
        countdownAttack.attackPower += fetchEnemyStatus(enemy.id, Status.STRENGTH)
        eventBus.dispatch(EnemyCountdownAttackEvent(enemy, countdownAttack))
    }

    suspend fun countdownAttackTick(enemyId: Int) {
        val countdownAttack = combat.enemyAttacks[enemyId]!!
        enemyDiscard(enemyId, countdownAttack.discardTile(runData.rng))
        countdownAttack.turnsLeft -= 1
        if (countdownAttack.turnsLeft == 0) {
            combat.enemyAttacks.remove(enemyId)
            eventBus.dispatch(CountdownAttackResolveEvent(countdownAttack))
            val baseDamage = countdownAttack.attackPower - countdownAttack.counteredAttackPower
            val damage = calculateActualDamage(
                    DamageOrigin.ENEMY_ATTACK,
                    DamageTarget.HERO,
                    combat.enemyStatus[enemyId]!!,
                    combat.heroStatus,
                    countdownAttack.element,
                    baseDamage)
            dealDamageToHero(damage)
            countdownAttack.activateEffects(this)
        } else {
            eventBus.dispatch(CountdownAttackTickEvent(countdownAttack))
        }
    }

    suspend fun enemyDiscard(enemyId: Int, tile: Tile) {
        val tileInstance = TileInstance(tile, nextId())
        combat.openPool.add(tileInstance)
        eventBus.dispatch(EnemyDiscardEvent(enemyId, tileInstance, combat.openPool.size - 1))
    }

    fun changeStatus(status: Status, amount: Int) {
        combat.heroStatus[status] = amount
    }

    fun changeStatusIncrement(status: Status, increment: Int) {
        combat.heroStatus.increment(status, increment)
    }

    fun changeEnemyStatus(id: Int, status: Status, amount: Int) {
        combat.enemyStatus[id]!![status] = amount
    }

    fun changeEnemyStatusIncrement(id: Int, status: Status, increment: Int) {
        combat.enemyStatus[id]!!.increment(status, increment)
    }

    fun fetchEnemyStatus(id: Int, status: Status) = combat.enemyStatus[id]!![status]

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

enum class DamageOrigin {
    HERO_ATTACK, HERO_MISC, ENEMY_ATTACK, ENEMY_MISC
}

enum class DamageTarget {
    HERO, ENEMY, ENEMY_SPELL
}
