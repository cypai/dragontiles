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
        combat.sideboard.addAll(runData.hero.sideboard)
        combat.enemies.forEach {
            it.preInit(api.nextId())
            combat.enemyStatus[it.id] = mutableListOf()
            eventBus.register(it)
            runBlocking { it.init(api) }
        }
        initDrawPile()
        runBlocking { api.drawToOpenPool(CombatApi.OPEN_POOL_SIZE) }
        combat.spells.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        combat.sideboard.forEach {
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
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.FIRE, i), TileStatus.NONE, api.nextId()))
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.ICE, i), TileStatus.NONE, api.nextId()))
                combat.drawPile.add(TileInstance(Tile.ElementalTile(Suit.LIGHTNING, i), TileStatus.NONE, api.nextId()))
            }
        }
        repeat(4) {
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.EARTH), TileStatus.NONE, api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.MOON), TileStatus.NONE, api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.SUN), TileStatus.NONE, api.nextId()))
            combat.drawPile.add(TileInstance(Tile.StarTile(StarType.STAR), TileStatus.NONE, api.nextId()))
        }
        repeat(4) {
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.LIFE), TileStatus.NONE, api.nextId()))
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.MIND), TileStatus.NONE, api.nextId()))
            combat.drawPile.add(TileInstance(Tile.LifeTile(LifeType.SOUL), TileStatus.NONE, api.nextId()))
        }
        combat.drawPile.shuffle(runData.rng)
    }

    suspend fun runTurn() {
        combat.turnNumber += 1
        if (combat.turnNumber > 1) {
            api.queryOpenPoolDraw()
        }
        if (combat.turnNumber == 1) {
            // Normally intents are changed after endTurn(), this inits the intents and avoids messing with Stunned
            combat.enemies
                .forEach {
                    api.changeEnemyIntent(it, it.getIntent())
                }
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
                val intent = if (api.enemyHasStatus(it, Overloaded::class)) {
                    StunnedIntent(it)
                } else {
                    it.nextIntent(api)
                }
                api.changeEnemyIntent(it, intent)
            }
        combat.spells.forEach { it.turnReset() }
        combat.sideboard.forEach { it.turnReset() }
        api.swapQuery(1)
        api.drawToOpenPool(1)
        runTurn()
    }

}
