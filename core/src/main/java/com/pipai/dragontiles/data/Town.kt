package com.pipai.dragontiles.data

data class Town(
    var actions: Int,
    val dungeonEventId: String?,
    val spellShop: SpellShop,
    val itemShop: ItemShop,
    val scribe: Scribe,
    var solicited: Boolean = false,
    var checkedEvent: Boolean = false,
    var boughtSpell: Boolean = false,
    var boughtSideboard: Boolean = false,
    var boughtItem: Boolean = false,
    var boughtUpgrade: Boolean = false,
)

data class SpellShop(
    val classSpells: MutableList<PricedItem>,
    val sorceries: MutableList<PricedItem>,
    var colorlessSpell: PricedItem?,
)

data class ItemShop(
    val relics: MutableList<PricedItem>,
)

data class Scribe(
    val upgrades: MutableList<PricedItem>,
)

data class PricedItem(override val id: String, val price: Int) : Localized
