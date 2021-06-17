package com.pipai.dragontiles.utils

import com.badlogic.gdx.math.Vector2
import kotlin.reflect.KClass

fun Vector2.withX(x: Float): Vector2 {
    val v = this.cpy()
    v.x = x
    return v
}

fun Vector2.withY(y: Float): Vector2 {
    val v = this.cpy()
    v.y = y
    return v
}

fun <T> List<T>.with(element: T): List<T> {
    val copy = this.toMutableList()
    copy.add(element)
    return copy
}

fun <T> List<T>.withAll(elements: List<T>): List<T> {
    val copy = this.toMutableList()
    copy.addAll(elements)
    return copy
}

fun <T> List<T>.withoutAll(elements: List<T>): List<T> {
    val copy = this.toMutableList()
    copy.removeAll(elements)
    return copy
}

fun <T> Set<T>.with(element: T): Set<T> {
    val copy = this.toMutableSet()
    copy.add(element)
    return copy
}

fun <T> Set<T>.withAll(elements: Set<T>): Set<T> {
    val copy = this.toMutableSet()
    copy.addAll(elements)
    return copy
}

fun <T> Set<T>.withoutAll(elements: Set<T>): Set<T> {
    val copy = this.toMutableSet()
    copy.removeAll(elements)
    return copy
}

inline fun <T : Any, reified U : T> List<T>.findAs(klass: KClass<U>): U? {
    val item = this.find { klass.isInstance(it) }
    return if (item != null && item is U) {
        item
    } else {
        null
    }
}

inline fun <T : Any, reified U : T> List<T>.findAsWhere(klass: KClass<U>, predicate: (U) -> Boolean): U? {
    val item = this.find { klass.isInstance(it) && predicate(it as U) }
    return if (item != null && item is U) {
        item
    } else {
        null
    }
}
