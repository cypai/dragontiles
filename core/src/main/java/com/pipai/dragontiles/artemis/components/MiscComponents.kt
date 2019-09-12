package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class LineComponent : Component() {
    lateinit var start: Vector2
    lateinit var end: Vector2
    var color = Color.BLACK
}

class MouseFollowComponent : Component()
