package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.data.Reward
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.status.Status

data class Combat(val enemies: List<Enemy>) {

    var turnNumber = 0

    val spells: MutableList<Spell> = mutableListOf()
    val sideboard: MutableList<Spell> = mutableListOf()
    val sorceries: MutableList<Sorcery> = mutableListOf()
    val relics: MutableList<Relic> = mutableListOf()

    val hand: MutableList<TileInstance> = mutableListOf()
    val drawPile: MutableList<TileInstance> = mutableListOf()
    val discardPile: MutableList<TileInstance> = mutableListOf()
    val openPool: MutableList<TileInstance> = mutableListOf()

    /**
     * StandardSpell Index -> List<TileInstance> in CombatApi
     * For assigned tiles in runes
     */
    val assigned: MutableMap<Int, List<TileInstance>> = mutableMapOf()

    val heroStatus: MutableList<Status> = mutableListOf()
    val enemyStatus: MutableMap<Int, MutableList<Status>> = mutableMapOf()
    val enemyIntent: MutableMap<Int, Intent> = mutableMapOf()

    fun init(gameData: GameData, runData: RunData) {
        spells.addAll(runData.hero.generateSpells(gameData))
        sideboard.addAll(runData.hero.generateSideboard(gameData))
        sorceries.addAll(runData.hero.generateSorceries(gameData))
        relics.addAll(runData.hero.relicIds.map { gameData.getRelic(it.id).withCounter(it.counter) })
    }
}
