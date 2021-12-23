package com.pipai.dragontiles.data

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
    val relicIds: MutableList<String>,
    var gold: Int,
    val potionSlots: MutableList<PotionSlot>,
)

data class PotionSlot(var potionId: String?)
