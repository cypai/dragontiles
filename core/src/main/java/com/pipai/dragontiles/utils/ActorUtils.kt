package com.pipai.dragontiles.utils

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor

fun Actor.boundingRectangle() = Rectangle(x, y, width, height)
