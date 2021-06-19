package com.pipai.dragontiles.combat

import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.Rune
import com.pipai.dragontiles.spells.StandardSpell
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.deepCopy
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

class CombatApi(
    val runData: RunData,
    val combat: Combat,
    private val eventBus: SuspendableEventBus
) : GlobalApi(runData) {

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    fun register(o: Any) {
        eventBus.register(o)
    }

    suspend fun exhaust(spell: StandardSpell) {
        spell.exhausted = true
        eventBus.dispatch(SpellExhaustedEvent(spell))
    }

    fun numTilesInHand(): Int {
        return combat.hand.size + combat.assigned.values.map { it.size }.sum()
    }

    fun getEnemy(id: Int): Enemy {
        return combat.enemies.first { it.id == id }
    }

    suspend fun castSpell(spell: StandardSpell) {
        eventBus.dispatch(SpellCastedEvent(spell))
    }

    suspend fun activateRune(rune: Rune, components: List<TileInstance>) {
        val runeIndex = combat.spells.indexOf(rune)
        combat.assigned[runeIndex] = components
        combat.hand.removeAll(components)
        eventBus.dispatch(RuneActivatedEvent(rune))
    }

    suspend fun deactivateRune(rune: Rune) {
        val runeIndex = combat.spells.indexOf(rune)
        val components = combat.assigned.remove(runeIndex)!!
        combat.hand.addAll(components)
        eventBus.dispatch(RuneDeactivatedEvent(rune))
        sortHand()
    }

    suspend fun draw(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        val drawnDiscardTiles: MutableList<TileInstance> = mutableListOf()
        var currentSize = numTilesInHand()
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
        sortHand()
    }

    suspend fun transformTile(tileInstance: TileInstance, tile: Tile, sortHand: Boolean) {
        val newTile = TileInstance(tile, nextId())
        val index = combat.hand.indexOf(tileInstance)
        combat.hand.removeAt(index)
        combat.hand.add(index, newTile)
        eventBus.dispatch(TileTransformedEvent(newTile, tileInstance))
        if (sortHand) {
            sortHand()
        }
    }

    suspend fun destroyTile(tileInstance: TileInstance) {
        combat.hand.remove(tileInstance)
        eventBus.dispatch(TileDestroyedEvent(tileInstance))
        sortHand()
    }

    suspend fun addTilesToHand(tiles: List<Tile>) {
        val addedTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        val discardedTiles: MutableList<TileInstance> = mutableListOf()
        tiles.forEach {
            val tileInstance = TileInstance(it, nextId())
            if (numTilesInHand() < runData.hero.handSize) {
                combat.hand.add(tileInstance)
                addedTiles.add(Pair(tileInstance, combat.hand.size))
            } else {
                combat.discardPile.add(tileInstance)
                discardedTiles.add(tileInstance)
            }
        }
        eventBus.dispatch(TilesAddedToHandEvent(addedTiles))
        if (discardedTiles.isNotEmpty()) {
            eventBus.dispatch(TilesAddedDiscardedEvent(discardedTiles))
        }
        sortHand()
    }

    suspend fun openDiscard(tiles: List<TileInstance>) {
        combat.hand.removeAll(tiles)
        combat.openPool.addAll(tiles)
        eventBus.dispatch(OpenDiscardEvent(tiles))
        if (combat.openPool.size > 9) {
            removeFromOpenPool(combat.openPool.slice(0 until combat.openPool.size - 9))
        }
        sortHand()
    }

    suspend fun removeFromOpenPool(tiles: List<TileInstance>) {
        combat.openPool.removeAll(tiles)
        eventBus.dispatch(OpenPoolToDiscardEvent(tiles))
        eventBus.dispatch(OpenPoolAdjustedEvent(combat.openPool.toList()))
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
        if (combat.openPool.size > 9) {
            removeFromOpenPool(combat.openPool.slice(0 until combat.openPool.size - 9))
        }
    }

    fun calculateBaseDamage(element: Element, amount: Int): Int {
        var flat = 0
        var scaling = 1f
        runData.hero.relics.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        combat.spells.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        combat.heroStatus.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        return ((amount + flat) * scaling).toInt()
    }

    fun calculateDamageOnEnemy(enemy: Enemy, element: Element, amount: Int): Int {
        var flat = 0
        var scaling = 1f
        runData.hero.relics.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        combat.spells.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        combat.heroStatus.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        combat.enemyStatus[enemy.id]!!.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
        }
        return ((amount + flat) * scaling).toInt()
    }

    fun calculateDamageOnHero(enemy: Enemy, element: Element, amount: Int): Int {
        var flat = 0
        var scaling = 1f
        runData.hero.relics.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
        }
        combat.spells.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
        }
        combat.heroStatus.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.OPPONENT_ATTACK, DamageTarget.SELF, element)
        }
        combat.enemyStatus[enemy.id]!!.forEach {
            flat += it.queryFlatAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
            scaling *= it.queryScaledAdjustment(DamageOrigin.SELF_ATTACK, DamageTarget.OPPONENT, element)
        }
        return ((amount + flat) * scaling).toInt()
    }

    suspend fun attack(enemy: Enemy, element: Element, amount: Int) {
        val damage = calculateDamageOnEnemy(enemy, element, amount)
        eventBus.dispatch(PlayerAttackEnemyEvent(enemy, element, amount))
        if (enemy.flux < enemy.fluxMax) {
            dealFluxDamageToEnemy(enemy, damage)
        } else {
            dealDamageToEnemy(enemy, damage)
        }
    }

    suspend fun dealFluxDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.flux += damage
        eventBus.dispatch(EnemyFluxDamageEvent(enemy, damage))
        if (enemy.flux > enemy.fluxMax) {
            enemy.flux = enemy.fluxMax
            addStatusToEnemy(enemy, Overloaded(2))
        }
    }

    suspend fun enemyLoseFlux(enemy: Enemy, amount: Int) {
        val actualAmount = if (enemy.flux >= amount) amount else enemy.flux
        enemy.flux -= actualAmount
        eventBus.dispatch(EnemyLoseFluxEvent(enemy, actualAmount))
    }

    suspend fun changeEnemyIntent(enemy: Enemy, intent: Intent?) {
        if (intent == null) {
            combat.enemyIntent.remove(enemy.id)
        } else {
            combat.enemyIntent[enemy.id] = intent
        }
        eventBus.dispatch(EnemyChangeIntentEvent(enemy, intent))
    }

    suspend fun dealDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.hp -= damage
        eventBus.dispatch(EnemyDamageEvent(enemy, damage))
        if (enemy.hp <= 0) {
            combat.enemyIntent.remove(enemy.id)
            eventBus.dispatch(EnemyChangeIntentEvent(enemy, null))
            combat.enemyStatus[enemy.id]!!.clear()
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

    suspend fun assign(components: List<TileInstance>, rune: Rune) {
        combat.hand.removeAll(components)
        val runeIndex = combat.spells.indexOf(rune)
        combat.assigned[runeIndex]
        sortHand()
    }

    suspend fun attackHero(enemy: Enemy, element: Element, amount: Int) {
        val damage = calculateDamageOnHero(enemy, element, amount)
        if (runData.hero.flux < runData.hero.fluxMax) {
            dealFluxDamageToHero(damage)
        } else {
            dealDamageToHero(damage)
        }
    }

    suspend fun dealFluxDamageToHero(damage: Int) {
        runData.hero.flux += damage
        eventBus.dispatch(PlayerFluxDamageEvent(damage))
        if (runData.hero.flux > runData.hero.fluxMax) {
            runData.hero.flux = runData.hero.fluxMax
            addStatusToHero(Overloaded(2))
        }
    }

    suspend fun heroLoseFlux(amount: Int) {
        val actualAmount = if (runData.hero.flux >= amount) amount else runData.hero.flux
        runData.hero.flux -= actualAmount
        eventBus.dispatch(PlayerLoseFluxEvent(actualAmount))
    }

    suspend fun dealDamageToHero(damage: Int) {
        runData.hero.hp -= damage
        eventBus.dispatch(PlayerDamageEvent(damage))
        if (runData.hero.hp <= 0) {
            eventBus.dispatch(GameOverEvent())
        }
    }

    suspend fun addStatusToHero(status: Status) {
        status.combatant = Combatant.HeroCombatant
        val maybeStatus = combat.heroStatus.find { it.strId == status.strId }
        if (maybeStatus == null) {
            combat.heroStatus.add(status)
            eventBus.register(status)
        } else {
            maybeStatus.amount += status.amount
            if (maybeStatus.amount == 0) {
                removeHeroStatus(status::class)
            }
        }
        notifyStatusUpdated()
    }

    suspend fun addStatusToEnemy(enemy: Enemy, status: Status) {
        status.combatant = Combatant.EnemyCombatant(enemy)
        val enemyStatus = combat.enemyStatus[enemy.id]!!
        val maybeStatus = enemyStatus.find { it.strId == status.strId }
        if (maybeStatus == null) {
            enemyStatus.add(status)
            eventBus.register(status)
        } else {
            maybeStatus.amount += status.amount
            if (maybeStatus.amount == 0) {
                removeEnemyStatus(enemy, status::class)
            }
        }
        notifyStatusUpdated()
    }

    fun <T : Status> heroStatusAmount(statusType: KClass<T>): Int {
        val status = combat.heroStatus.find { statusType.isInstance(it) }
        return status?.amount ?: 0
    }

    fun <T : Status> heroHasStatus(statusType: KClass<T>): Boolean {
        return combat.heroStatus.any { statusType.isInstance(it) }
    }

    suspend fun <T : Status> removeHeroStatus(statusType: KClass<T>): Boolean {
        val item = combat.heroStatus.find { statusType.isInstance(it) }
        if (item != null) {
            combat.heroStatus.remove(item)
            eventBus.unregister(item)
            notifyStatusUpdated()
            return true
        }
        return false
    }

    fun <T : Status> enemyStatusAmount(enemy: Enemy, statusType: KClass<T>): Int {
        val status = combat.enemyStatus[enemy.id]!!.find { statusType.isInstance(it) }
        return status?.amount ?: 0
    }

    fun <T : Status> enemyHasStatus(enemy: Enemy, statusType: KClass<T>): Boolean {
        return combat.enemyStatus[enemy.id]!!.any { statusType.isInstance(it) }
    }

    suspend fun <T : Status> removeEnemyStatus(enemy: Enemy, statusType: KClass<T>): Boolean {
        val statusList = combat.enemyStatus[enemy.id]!!
        val item = statusList.find { statusType.isInstance(it) }
        if (item != null) {
            statusList.remove(item)
            eventBus.unregister(item)
            notifyStatusUpdated()
            return true
        }
        return false
    }

    suspend fun notifyStatusUpdated() {
        val enemyStatusCopy = combat.enemyStatus.mapValues { es -> es.value.map { s -> s.deepCopy() } }
        eventBus.dispatch(StatusAdjustedEvent(deepCopy(combat.heroStatus), enemyStatusCopy))
    }

    suspend fun queryTiles(
        text: String,
        tiles: List<TileInstance>,
        minAmount: Int,
        maxAmount: Int
    ): List<TileInstance> {
        return if (maxAmount == 0 || maxAmount < minAmount) {
            listOf()
        } else {
            suspendCoroutine {
                runBlocking {
                    eventBus.dispatch(QueryTilesEvent(text, tiles, minAmount, maxAmount, it))
                }
            }
        }
    }

    suspend fun queryOpenPoolDraw() {
        val amount = runData.hero.handSize - numTilesInHand()
        val tiles = queryTiles(
            "Select up to $amount tile(s) to draw from the Open Pool",
            combat.openPool, 0, amount
        )
        if (tiles.isNotEmpty()) {
            drawFromOpenPool(tiles)
        }
    }

    suspend fun queryTileOptions(
        text: String,
        displayTile: TileInstance?,
        options: List<Tile>,
        minAmount: Int,
        maxAmount: Int
    ): List<Tile> {

        return if (maxAmount == 0 || maxAmount < minAmount) {
            listOf()
        } else {
            suspendCoroutine {
                runBlocking {
                    eventBus.dispatch(QueryTileOptionsEvent(text, displayTile, options, minAmount, maxAmount, it))
                }
            }
        }
    }

    suspend fun queryTransform(tile: TileInstance, options: List<Tile>): Tile {
        return queryTileOptions("Transform this tile", tile, options, 1, 1).first()
    }

}

enum class DamageOrigin {
    SELF_ATTACK, SELF_MISC, OPPONENT_ATTACK, OPPONENT_MISC
}

enum class DamageTarget {
    SELF, OPPONENT
}
