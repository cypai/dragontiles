package com.pipai.dragontiles.hero

import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell

data class Hero(val name: String,
                var hp: Int,
                var hpMax: Int,
                var flux: Int,
                var fluxMax: Int,
                val handSize: Int,
                val spells: MutableList<Spell>,
                val relics: MutableList<Relic>,
                var gold: Int)
