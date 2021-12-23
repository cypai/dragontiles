package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeonevents.DungeonEvent
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import kotlinx.serialization.Serializable

@Serializable
data class Town(
    var actions: Int,
    var dungeonEvent: DungeonEvent?,
    val spellShop: SpellShop,
    val itemShop: ItemShop,
    val scribe: Scribe,
)

@Serializable
data class SpellShop(
    val classSpells: MutableList<PricedSpell>,
    val sorceries: MutableList<PricedSpell>,
    var colorlessSpell: PricedSpell?,
)

@Serializable
data class ItemShop(
    val relics: MutableList<PricedRelic>,
)

@Serializable
data class Scribe(
    val upgrades: MutableList<PricedUpgrade>,
)

@Serializable
data class PricedSpell(val spell: Spell, val price: Int)

@Serializable
data class PricedRelic(val relic: Relic, val price: Int)

@Serializable
data class PricedUpgrade(val upgrade: SpellUpgrade, val price: Int)
