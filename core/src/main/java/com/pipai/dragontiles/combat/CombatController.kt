package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.*
import net.mostlyoriginal.api.event.common.EventSystem

class CombatController(private val combat: Combat, private val eventSystem: EventSystem) {

    val api: CombatApi = CombatApi(combat, combat.hero.spells.map { it.createInstance() }.toList(), eventSystem)

    fun initCombat() {
        combat.enemies.forEach {
            it.preInit(api.nextId())
            it.init()
            combat.enemyStatus[it.id] = mutableMapOf()
        }
        initDrawPile()
        initOpenPile()
        api.spellInstances.forEach {
            eventSystem.registerEvents(it)
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
        combat.drawPile.shuffle(combat.rng)
    }

    private fun initOpenPile() {
        api.drawToOpenPool(9)
        api.sortOpenPool()
    }

    fun runTurn() {
        combat.turnNumber += 1
        combat.enemies.forEach {
            it.runTurn(api)
        }
        eventSystem.dispatch(TurnStartEvent(combat.turnNumber))
        if (combat.hero.handSize > combat.hand.size) {
            api.draw(combat.hero.handSize - combat.hand.size)
        }
        api.sortHand()
    }

    fun endTurn() {
        eventSystem.dispatch(TurnEndEvent(combat.turnNumber))
        combat.incomingAttacks.toList().forEach {
            api.updateCountdownAttack(it)
        }
        api.spellInstances.forEach {
            it.turnReset(api)
        }
        runTurn()
    }

}
