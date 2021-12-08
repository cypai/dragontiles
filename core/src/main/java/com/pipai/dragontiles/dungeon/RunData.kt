package com.pipai.dragontiles.dungeon

import com.pipai.dragontiles.data.Town
import com.pipai.dragontiles.hero.Hero
import java.util.*

data class RunData(val rng: Random, val hero: Hero, var dungeon: Dungeon, var town: Town?)
