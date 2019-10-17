package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.enemies.Enemy

data class Encounter(val enemies: List<Pair<Enemy, Vector2>>)
