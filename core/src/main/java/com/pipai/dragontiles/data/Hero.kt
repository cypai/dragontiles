package com.pipai.dragontiles.data

import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellInstance

data class Hero(
    val heroClassId: String,
    val name: String,
    var hp: Int,
    var hpMax: Int,
    var flux: Int,
    var fluxMax: Int,
    val handSize: Int,
    val spells: MutableList<SpellInstance>,
    val spellsSize: Int,
    val sideboard: MutableList<SpellInstance>,
    var sideboardSize: Int,
    val sorceries: MutableList<SpellInstance>,
    var sorceriesSize: Int,
    val relicIds: MutableList<RelicInstance>,
    var gold: Int,
    val potionSlots: MutableList<PotionSlot>,
) {

    fun generateSpells(gameData: GameData): List<Spell> {
        return generateSpellImpl(spells, gameData)
    }

    fun generateSideboard(gameData: GameData): List<Spell> {
        return generateSpellImpl(sideboard, gameData)
    }

    fun generateSorceries(gameData: GameData): List<Sorcery> {
        return generateSpellImpl(sorceries, gameData).filterIsInstance<Sorcery>().toList()
    }

    private fun generateSpellImpl(spellList: List<SpellInstance>, gameData: GameData): List<Spell> {
        return spellList.map { it.toSpell(gameData) }
    }
}

data class PotionSlot(var potionId: String?)
