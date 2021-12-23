package com.pipai.dragontiles.hero

import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Sorcery
import com.pipai.dragontiles.spells.Spell

data class Hero(val heroClass: HeroClass,
                val name: String,
                var hp: Int,
                var hpMax: Int,
                var flux: Int,
                var fluxMax: Int,
                val handSize: Int,
                val spells: MutableList<Spell>,
                val spellsSize: Int,
                val sideboard: MutableList<Spell>,
                var sideboardSize: Int,
                val sorceries: MutableList<Sorcery>,
                var sorceriesSize: Int,
                val relics: MutableList<Relic>,
                var gold: Int,
                val potionSlots: MutableList<PotionSlot>,
)

data class PotionSlot(var potion: Potion?)
