package com.pipai.dragontiles.hero

import com.pipai.dragontiles.spells.Spell

data class Hero(val name: String, var hp: Int, var hpMax: Int, val handSize: Int, val spells: MutableList<Spell>)
