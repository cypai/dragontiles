package com.pipai.dragontiles.utils

import com.badlogic.gdx.math.Rectangle
import com.esotericsoftware.spine.Skeleton
import kotlin.math.abs

fun Skeleton.boundingRectangle(): Rectangle {
    val actualWidth = this.data.width * abs(this.scaleX)
    val actualHeight = this.data.height * abs(this.scaleY)
    return Rectangle(this.x - actualWidth / 2f, this.y, actualWidth, actualHeight)
}
