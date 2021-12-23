package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.enemies.Enemy

data class Encounter(override val id: String, val enemies: List<Pair<Enemy, Vector2>>) : Localized
