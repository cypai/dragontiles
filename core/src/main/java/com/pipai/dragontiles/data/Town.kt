package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellUpgrade

data class Town(
    var actions: Int,
    var dungeonEvent: DungeonEvent?,
    val spellShop: SpellShop,
    val itemShop: ItemShop,
    val scribe: Scribe,
)

data class SpellShop(
    val classSpells: MutableList<PricedSpell>,
    val sorceries: MutableList<PricedSpell>,
    var cantrip: PricedSpell,
    var colorlessSpell: PricedSpell,
)

data class ItemShop(
    val relics: MutableList<PricedRelic>,
)

data class Scribe(
    val upgrades: MutableList<PricedUpgrade>,
)

data class PricedSpell(val spell: Spell, val price: Int)
data class PricedRelic(val relic: Relic, val price: Int)
data class PricedUpgrade(val upgrade: SpellUpgrade, val price: Int)
