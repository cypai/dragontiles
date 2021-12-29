package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Dodge
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

class CombatApi(
    gameData: GameData,
    runData: RunData,
    val combat: Combat,
    private val eventBus: CombatEventBus,
) : GlobalApi(gameData, runData, eventBus.sEvent) {

    companion object {
        const val OPEN_POOL_SIZE = 9
    }

    private val logger = getLogger()
    private val rng = runData.seed.miscRng()
    private var nextId = 0
    val swapChannel = Channel<SwapData>()

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
        return combat.enemies.first { it.enemyId == id }
    }

    suspend fun castSpell(spell: Spell) {
        eventBus.dispatch(SpellCastedEvent(spell))
        val flux = spell.baseFluxGain()
        if (runData.hero.flux + flux >= runData.hero.tempFluxMax) {
            logger.error("Attempted to cast spell that would cause self-overload")
            return
        }
        if (spell is StandardSpell && spell.aspects.any { it is ExhaustAspect }) {
            spell.exhausted = true
        }
        dealFluxDamageToHero(flux)
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

    suspend fun setTileStatus(tiles: List<TileInstance>, tileStatus: TileStatus) {
        tiles.forEach { it.tileStatus = tileStatus }
        eventBus.dispatch(TileStatusChangeEvent(tiles, tileStatus))
    }

    suspend fun inflictTileStatusOnHand(strategy: TileStatusInflictStrategy) {
        val selected = strategy.select(combat.hand, rng).toMutableList()
        if (selected.size < strategy.amount) {
            when (strategy.notEnoughStrategy) {
                TileStatusInflictStrategy.NotEnoughStrategy.RANDOM -> {
                    selected.addAll(combat.hand.withoutAll(selected).chooseAmount(strategy.amount - selected.size, rng))
                }
                else -> {
                }
            }
        }
        setTileStatus(selected, strategy.tileStatus)
    }

    suspend fun transformTile(tileInstance: TileInstance, tile: Tile, sortHand: Boolean) {
        if (tileInstance.tileStatus == TileStatus.FREEZE) {
            logger.error("Attempted to transform a frozen tile")
            return
        }
        val newTile = TileInstance(tile, TileStatus.NONE, nextId())
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

    suspend fun addTilesToHand(tiles: List<Tile>, status: TileStatus) {
        val addedTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        val discardedTiles: MutableList<TileInstance> = mutableListOf()
        tiles.forEach {
            val tileInstance = TileInstance(it, status, nextId())
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
        if (combat.openPool.size > OPEN_POOL_SIZE) {
            removeFromOpenPool(combat.openPool.slice(0 until combat.openPool.size - OPEN_POOL_SIZE))
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

    suspend fun fetch() {
        if (combat.openPool.size < OPEN_POOL_SIZE) {
            drawToOpenPool(OPEN_POOL_SIZE - combat.openPool.size)
        }
    }

    suspend fun fetch(amount: Int) {
        drawToOpenPool(amount)
    }

    suspend fun drawToOpenPool(amount: Int) {
        val drawnTiles: MutableList<Pair<TileInstance, Int>> = mutableListOf()
        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            combat.openPool.add(tile)
            drawnTiles.add(Pair(tile, combat.openPool.size - 1))
        }
        eventBus.dispatch(DrawToOpenPoolEvent(drawnTiles))
        if (combat.openPool.size > OPEN_POOL_SIZE) {
            removeFromOpenPool(combat.openPool.slice(0 until combat.openPool.size - OPEN_POOL_SIZE))
        }
    }

    suspend fun swapQuery(amount: Int) {
        if (combat.sideboard.isEmpty() || combat.sideboard.none { it.swappableFromSideboard() }) {
            return
        }
        eventBus.dispatch(QuerySwapEvent(amount))
        val data = swapChannel.receive()
        swap(data.activeIndexes, data.sideboardIndexes)
    }

    suspend fun swap(activeIndexes: List<Int>, sideboardIndexes: List<Int>) {
        if (activeIndexes.map { combat.spells[it] }.any { it is Rune && it.active }) {
            logger.error("Attempted to swap an active rune.")
            return
        }
        if (activeIndexes.size != sideboardIndexes.size) {
            logger.error("Swap size not equal.")
            return
        }
        if (activeIndexes.isNotEmpty()) {
            val activeSpells = mutableListOf<Spell>()
            val sideboardSpells = mutableListOf<Spell>()
            activeIndexes.zip(sideboardIndexes).forEach { (activeIndex, sideIndex) ->
                val activeSpell = combat.spells[activeIndex]
                activeSpells.add(activeSpell)
                sideboardSpells.add(combat.sideboard[sideIndex])
                combat.spells[activeIndex] = combat.sideboard[sideIndex]
                combat.sideboard[sideIndex] = activeSpell
            }
            eventBus.dispatch(SwapEvent(activeIndexes, sideboardIndexes, activeSpells, sideboardSpells))
        }
    }

    /**
     * Only used for dynamic variables, NOT used in API to prevent double-counting.
     */
    fun calculateBaseDamage(element: Element, amount: Int): Int {
        var flat = 0
        var scaling = 1f
        val allQueryRespondents: List<DamageAdjustable> = combat.relics
            .withAll(combat.spells)
            .withAll(combat.heroStatus)
        allQueryRespondents.forEach {
            flat += it.queryFlatAdjustment(Combatant.HeroCombatant, null, element, listOf())
            scaling *= it.queryScaledAdjustment(Combatant.HeroCombatant, null, element, listOf())
        }
        return ((amount + flat) * scaling).toInt()
    }

    fun calculateDamageOnEnemy(enemy: Enemy, element: Element, amount: Int, flags: List<CombatFlag>): Int {
        var flat = 0
        var scaling = 1f
        val allQueryRespondents: List<DamageAdjustable> = combat.relics
            .withAll(combat.spells)
            .withAll(combat.heroStatus)
            .withAll(combat.enemyStatus[enemy.enemyId]!!)
        val allFlags: MutableList<CombatFlag> = mutableListOf()
        allQueryRespondents.forEach {
            allFlags.addAll(
                it.queryForAdditionalFlags(
                    Combatant.HeroCombatant,
                    Combatant.EnemyCombatant(enemy),
                    element,
                    flags
                )
            )
        }
        allQueryRespondents.forEach {
            flat += it.queryFlatAdjustment(Combatant.HeroCombatant, Combatant.EnemyCombatant(enemy), element, allFlags)
            scaling *= it.queryScaledAdjustment(
                Combatant.HeroCombatant,
                Combatant.EnemyCombatant(enemy),
                element,
                allFlags
            )
        }
        return ((amount + flat) * scaling).toInt()
    }

    fun calculateDamageOnHero(enemy: Enemy, element: Element, amount: Int): Int {
        var flat = 0
        var scaling = 1f
        val allQueryRespondents: List<DamageAdjustable> = combat.relics
            .withAll(combat.spells)
            .withAll(combat.heroStatus)
            .withAll(combat.enemyStatus[enemy.enemyId]!!)
        allQueryRespondents.forEach {
            flat += it.queryFlatAdjustment(Combatant.EnemyCombatant(enemy), Combatant.HeroCombatant, element, listOf())
            scaling *= it.queryScaledAdjustment(
                Combatant.EnemyCombatant(enemy),
                Combatant.HeroCombatant,
                element,
                listOf()
            )
        }
        return ((amount + flat) * scaling).toInt()
    }

    suspend fun attack(
        enemy: Enemy,
        element: Element,
        amount: Int,
        flags: List<CombatFlag>,
    ) {
        val damage = calculateDamageOnEnemy(enemy, element, amount, flags)
        val asAttack = flags.contains(CombatFlag.ATTACK)
        if (asAttack) {
            eventBus.dispatch(PlayerAttackEnemyEvent(enemy, element, amount))
        }
        if (asAttack && enemyHasStatus(enemy, Dodge::class)) {
            addStatusToEnemy(enemy, Dodge(-1))
        } else {
            if (!flags.contains(CombatFlag.PIERCING) && enemy.flux < enemy.fluxMax) {
                dealFluxDamageToEnemy(enemy, damage)
            } else {
                dealDamageToEnemy(enemy, damage)
            }
        }
    }

    suspend fun aoeAttack(
        element: Element,
        amount: Int,
        flags: List<CombatFlag>,
    ) {
        combat.enemies.filter { it.hp > 0 }
            .forEach { attack(it, element, amount, flags) }
    }

    suspend fun dealFluxDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.flux += damage
        eventBus.dispatch(EnemyFluxDamageEvent(enemy, damage))
        if (enemy.flux >= enemy.fluxMax) {
            enemy.flux = enemy.fluxMax
            addStatusToEnemy(enemy, Overloaded(2))
            changeEnemyIntent(enemy, StunnedIntent(enemy))
        }
    }

    suspend fun enemyLoseFlux(enemy: Enemy, amount: Int) {
        val actualAmount = if (enemy.flux >= amount) amount else enemy.flux
        enemy.flux -= actualAmount
        eventBus.dispatch(EnemyLoseFluxEvent(enemy, actualAmount))
    }

    suspend fun changeEnemyIntent(enemy: Enemy, intent: Intent?) {
        if (intent == null) {
            combat.enemyIntent.remove(enemy.enemyId)
        } else {
            combat.enemyIntent[enemy.enemyId] = intent
        }
        eventBus.dispatch(EnemyChangeIntentEvent(enemy, intent))
    }

    suspend fun dealDamageToEnemy(enemy: Enemy, damage: Int) {
        enemy.hp -= damage
        eventBus.dispatch(EnemyDamageEvent(enemy, damage))
        if (enemy.hp <= 0) {
            combat.enemyIntent.remove(enemy.enemyId)
            val statuses = combat.enemyStatus[enemy.enemyId]!!
            statuses.forEach { eventBus.unregister(it) }
            statuses.clear()
            eventBus.unregister(enemy)
            eventBus.dispatch(EnemyDefeatedEvent(enemy))
            if (combat.enemies.all { it.hp <= 0 }) {
                combat.heroStatus.clear()
                eventBus.dispatch(BattleWinEvent())
            }
        }
    }

    suspend fun consume(components: List<TileInstance>, spell: Spell) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        eventBus.dispatch(ComponentConsumeEvent(components))
        components.forEach { tile ->
            when (tile.tileStatus) {
                TileStatus.BURN -> dealDamageToHero(2)
                TileStatus.SHOCK -> shockSpell(spell)
                TileStatus.VOLATILE -> dealFluxDamageToHero(2 * spell.baseFluxGain())
                TileStatus.CURSE -> changeTemporaryMaxFlux(2 * spell.baseFluxGain())
                else -> {
                }
            }
        }
        sortHand()
    }

    suspend fun shockSpell(spell: Spell) {
        if (spell is StandardSpell) {
            spell.shockTurns = 2
            eventBus.dispatch(SpellShockedEvent(spell))
        }
    }

    suspend fun sorceryConsume() {
        val components = combat.hand.toList()
        combat.hand.clear()
        combat.discardPile.addAll(components)
        eventBus.dispatch(ComponentConsumeEvent(components))
        sortHand()
    }

    suspend fun attackHero(enemy: Enemy, element: Element, amount: Int, piercing: Boolean = false) {
        if (heroHasStatus(Dodge::class)) {
            addStatusToHero(Dodge(-1))
        } else {
            val damage = calculateDamageOnHero(enemy, element, amount)
            if (!piercing && runData.hero.flux < runData.hero.tempFluxMax) {
                dealFluxDamageToHero(damage)
            } else {
                dealDamageToHero(damage)
            }
        }
    }

    suspend fun dealFluxDamageToHero(damage: Int) {
        runData.hero.flux += damage
        eventBus.dispatch(PlayerFluxDamageEvent(damage))
        if (runData.hero.flux >= runData.hero.tempFluxMax) {
            runData.hero.flux = runData.hero.tempFluxMax
            addStatusToHero(Overloaded(2))
        }
    }

    suspend fun heroLoseFlux(amount: Int) {
        val actualAmount = if (runData.hero.flux >= amount) amount else runData.hero.flux
        runData.hero.flux -= actualAmount
        eventBus.dispatch(PlayerLoseFluxEvent(actualAmount))
    }

    suspend fun changeTemporaryMaxFlux(amount: Int) {
        runData.hero.tempFluxMax += amount
        eventBus.dispatch(PlayerTempMaxFluxChangeEvent(amount))
    }

    suspend fun dealDamageToHero(damage: Int) {
        runData.hero.hp -= damage
        eventBus.dispatch(PlayerDamageEvent(damage))
        if (runData.hero.hp <= 0) {
            eventBus.dispatch(GameOverEvent())
        }
    }

    suspend fun healHero(amount: Int) {
        runData.hero.hp += amount
        if (runData.hero.hp > runData.hero.hpMax) {
            runData.hero.hp = runData.hero.hpMax
            eventBus.dispatch(PlayerHealEvent(amount))
        }
    }

    suspend fun addStatusToHero(status: Status) {
        status.combatant = Combatant.HeroCombatant
        val maybeStatus = combat.heroStatus.find { it.id == status.id }
        if (maybeStatus == null) {
            combat.heroStatus.add(status)
            eventBus.register(status)
            status.onInflict(this)
        } else {
            maybeStatus.amount += status.amount
            maybeStatus.onInflict(this)
            if (maybeStatus.amount == 0) {
                removeHeroStatus(status::class)
            }
        }
        notifyStatusUpdated()
    }

    suspend fun devInstantWin() {
        aoeAttack(Element.NONE, 99999, flags = listOf(CombatFlag.PIERCING))
    }

    suspend fun addAoeStatus(status: Status) {
        combat.enemies.filter { it.hp > 0 }
            .forEach { addStatusToEnemy(it, status.deepCopy()) }
    }

    suspend fun addStatusToEnemy(enemy: Enemy, status: Status) {
        status.combatant = Combatant.EnemyCombatant(enemy)
        val enemyStatus = combat.enemyStatus[enemy.enemyId]!!
        val maybeStatus = enemyStatus.find { it.id == status.id }
        if (maybeStatus == null) {
            enemyStatus.add(status)
            eventBus.register(status)
            eventBus.dispatch(EnemyStatusChangeEvent(enemy, status, 0))
            status.onInflict(this)
        } else {
            val previousAmount = maybeStatus.amount
            maybeStatus.amount += status.amount
            if (maybeStatus.amount == 0) {
                // No dispatch since removeEnemyStatus calls the EnemyStatusChangeEvent
                removeEnemyStatus(enemy, status::class)
            } else {
                eventBus.dispatch(EnemyStatusChangeEvent(enemy, maybeStatus, previousAmount))
                maybeStatus.onInflict(this)
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
        val status = combat.enemyStatus[enemy.enemyId]!!.find { statusType.isInstance(it) }
        return status?.amount ?: 0
    }

    fun <T : Status> enemyHasStatus(enemy: Enemy, statusType: KClass<T>): Boolean {
        return combat.enemyStatus[enemy.enemyId]!!.any { statusType.isInstance(it) }
    }

    suspend fun <T : Status> removeEnemyStatus(enemy: Enemy, statusType: KClass<T>): Boolean {
        val statusList = combat.enemyStatus[enemy.enemyId]!!
        val item = statusList.find { statusType.isInstance(it) }
        if (item != null) {
            item.amount = 0
            eventBus.dispatch(EnemyStatusChangeEvent(enemy, item, item.amount))
            statusList.remove(item)
            eventBus.unregister(item)
            notifyStatusUpdated()
            return true
        }
        return false
    }

    suspend fun notifyStatusUpdated() {
        combat.heroStatus.removeAll { it.amount <= 0 }
        combat.enemyStatus.values.forEach { es -> es.removeAll { it.amount <= 0 } }
        val enemyStatusCopy = combat.enemyStatus.mapValues { es -> es.value.map { s -> s.deepCopy() } }
        eventBus.dispatch(StatusOverviewAdjustedEvent(deepCopy(combat.heroStatus), enemyStatusCopy))
    }

    suspend fun queryTiles(
        text: String,
        tiles: List<TileInstance>,
        minAmount: Int,
        maxAmount: Int
    ): List<TileInstance> {
        val retval: List<TileInstance> = if (maxAmount == 0 || maxAmount < minAmount) {
            listOf()
        } else {
            suspendCoroutine {
                runBlocking {
                    eventBus.dispatch(QueryTilesEvent(text, tiles, minAmount, maxAmount, it))
                }
            }
        }
        sortHand()
        return retval
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

    suspend fun castSorceries(fullCastHand: FullCastHand) {
        combat.sorceries.forEach { sorcery ->
            when (val req = sorcery.requirement) {
                is Identical -> {
                    when (req.reqAmount.amount) {
                        2 -> {
                            sorcery.fill(fullCastHand.eye)
                            sorcery.onCast(fullCastHand, this)
                        }
                        3 -> {
                            fullCastHand.melds.filter { meld -> meld.type == MeldType.IDENTICAL }
                                .forEach { meld ->
                                    sorcery.fill(meld.tiles)
                                    sorcery.onCast(fullCastHand, this)
                                }
                        }
                        else -> {
                            req.find(combat.hand)
                                .forEach { match ->
                                    sorcery.fill(match)
                                    sorcery.onCast(fullCastHand, this)
                                }
                        }
                    }
                }
                is Sequential -> {
                    when (req.reqAmount.amount) {
                        3 -> {
                            fullCastHand.melds.filter { meld -> meld.type == MeldType.SEQUENCE }
                                .forEach { meld ->
                                    sorcery.fill(meld.tiles)
                                    sorcery.onCast(fullCastHand, this)
                                }
                        }
                        9 -> {
                            req.find(combat.hand).firstOrNull()?.let { match ->
                                sorcery.fill(match)
                                sorcery.onCast(fullCastHand, this)
                            }
                        }
                    }
                }
                else -> {
                    if (req.satisfied(fullCastHand)) {
                        sorcery.fill(combat.hand)
                        sorcery.onCast(fullCastHand, this)
                    }
                }
            }
        }
        sorceryConsume()
    }

    suspend fun usePotionInCombat(target: Int?, index: Int) {
        val potionSlot = runData.hero.potionSlots[index]
        val potion = gameData.getPotion(potionSlot.potionId!!)
        potionSlot.potionId = null
        potion.onCombatUse(target, this)
        eventBus.dispatch(PotionUseEvent(potion))
    }
}
