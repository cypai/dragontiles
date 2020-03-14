package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.RunData
import kotlinx.coroutines.runBlocking
import net.mostlyoriginal.api.event.common.EventSystem

class CombatController(private val runData: RunData,
                       private val combat: Combat,
                       eventSystem: EventSystem) {

    private val eventBus = SuspendableEventBus(eventSystem)
    val api: CombatApi = CombatApi(runData, combat, eventBus)

    fun initCombat() {
        eventBus.init(api)
        combat.spells.addAll(runData.hero.spells)
        combat.enemies.forEach {
            it.preInit(api.nextId())
            it.init(api)
            combat.enemyStatus[it.id] = StatusData()
        }
        initDrawPile()
        runBlocking { api.drawToOpenPool(9) }
        combat.spells.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        runData.hero.relics.forEach {
            eventBus.register(it)
        }
    }

    private fun initDrawPile() {
        for (i in 1..9) {
            repeat(4) {
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.FIRE, i), api.nextId()))
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.ICE, i), api.nextId()))
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, i), api.nextId()))
            }
        }
        repeat(4) {
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.EARTH), api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.MOON), api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.SUN), api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.STAR), api.nextId()))
        }
        repeat(4) {
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.LIFE), api.nextId()))
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.MIND), api.nextId()))
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.SOUL), api.nextId()))
        }
        combat.drawPile.shuffle(runData.rng)
    }

    suspend fun runTurn() {
        combat.turnNumber += 1
        combat.enemies
                .filter { it.hp > 0 }
                .forEach { it.runTurn(api) }
        if (combat.turnNumber > 1) {
            api.queryOpenPoolDraw()
        }
        if (runData.hero.handSize > combat.hand.size) {
            api.draw(runData.hero.handSize - combat.hand.size - combat.assigned.values.map { it.size }.sum())
        }
        eventBus.dispatch(TurnStartEvent(combat.turnNumber))
    }

    suspend fun endTurn() {
        eventBus.dispatch(TurnEndEvent(combat.turnNumber))
        combat.enemyAttacks.keys.toList().forEach {
            api.countdownAttackTick(it)
        }
        combat.heroStatus.decrementAll()
        combat.enemyStatus.values.forEach { it.decrementAll() }
        eventBus.dispatch(StatusAdjustedEvent(
                combat.heroStatus.pairs(),
                combat.enemyStatus.mapValues { it.value.pairs() }))
        combat.spells.forEach {
            it.turnReset()
        }
        runTurn()
    }

}
