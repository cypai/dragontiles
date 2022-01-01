package com.pipai.dragontiles.combat

import com.pipai.dragontiles.artemis.systems.animation.DelayAnimation
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.spells.StandardSpell
import com.pipai.dragontiles.status.Overloaded
import kotlinx.coroutines.runBlocking
import net.mostlyoriginal.api.event.common.EventSystem

class CombatController(
    private val gameData: GameData,
    private val runData: RunData,
    private val combat: Combat,
    eventSystem: EventSystem,
) {

    private val eventBus = CombatEventBus(eventSystem)
    val api: CombatApi = CombatApi(gameData, runData, combat, eventBus)

    fun init() {
        eventBus.init(api)
        combat.init(gameData, runData)
        combat.enemies.forEach {
            it.preInit(api.nextId())
            combat.enemyStatus[it.enemyId] = mutableListOf()
            eventBus.register(it)
            runBlocking { it.init(api) }
        }
        combat.spells.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        combat.sideboard.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        combat.sorceries.forEach {
            it.combatReset()
            eventBus.register(it)
        }
        combat.relics.forEach {
            eventBus.register(it)
        }
    }

    fun initCombat() {
        initDrawPile()
        runBlocking { api.drawToOpenPool(CombatApi.OPEN_POOL_SIZE) }
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
        combat.drawPile.shuffle(runData.seed.miscRng())
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
                    api.changeEnemyIntent(it, it.getIntent(api))
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
                combat.enemyIntent[it.enemyId]?.execute(api)
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
        combat.spells.forEach {
            if (it is StandardSpell && it.shockTurns > 0) {
                it.shockTurns--
            }
            it.turnReset()
        }
        combat.sideboard.forEach {
            if (it is StandardSpell && it.shockTurns > 0) {
                it.shockTurns--
            }
            it.turnReset()
        }
        api.swapQuery(1)
        api.drawToOpenPool(1)
        runTurn()
    }

}
