package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.status.Overloaded
import kotlinx.coroutines.runBlocking
import net.mostlyoriginal.api.event.common.EventSystem

class CombatController(
    private val runData: RunData,
    private val combat: Combat,
    eventSystem: EventSystem
) {

    private val eventBus = CombatEventBus(eventSystem)
    val api: CombatApi = CombatApi(runData, combat, eventBus)

    fun initCombat() {
        eventBus.init(api)
        combat.spells.addAll(runData.hero.spells)
        combat.sideDeck.addAll(runData.hero.sideDeck)
        combat.enemies.forEach {
            it.preInit(api.nextId())
            it.init(api)
            combat.enemyStatus[it.id] = mutableListOf()
        }
        initDrawPile()
        runBlocking { api.drawToOpenPool(CombatApi.OPEN_POOL_SIZE) }
        combat.spells.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        combat.sideDeck.forEach {
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
            .forEach {
            val intent = if (api.enemyHasStatus(it, Overloaded::class)) {
                StunnedIntent(it)
            } else {
                it.getIntent()
            }
            api.changeEnemyIntent(it, intent)
        }
        if (combat.turnNumber > 1) {
            api.queryOpenPoolDraw()
        }
        api.draw(runData.hero.handSize - combat.hand.size - combat.assigned.values.map { it.size }.sum())
        eventBus.dispatch(TurnStartEvent(combat.turnNumber))
    }

    suspend fun endTurn() {
        eventBus.dispatch(TurnEndEvent(combat.turnNumber))
        eventBus.dispatch(EnemyTurnStartEvent(combat.turnNumber))
        combat.enemies
            .filter { it.hp > 0 }
            .forEach {
                combat.enemyIntent[it.id]?.execute(api)
                api.changeEnemyIntent(it, null)
            }
        eventBus.dispatch(EnemyTurnEndEvent(combat.turnNumber))
        combat.enemies
            .filter { it.hp > 0 }
            .forEach {
                api.changeEnemyIntent(it, it.nextIntent(api))
            }
        combat.spells.forEach { it.turnReset() }
        combat.sideDeck.forEach { it.turnReset() }
        api.swapQuery(1)
        api.drawToOpenPool(1)
        runTurn()
    }

}
