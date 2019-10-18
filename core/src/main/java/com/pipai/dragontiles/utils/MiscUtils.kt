package com.pipai.dragontiles.utils

fun <T> List<T>.with(element: T) : List<T>{
    val copy = this.toMutableList()
    copy.add(element)
    return copy
}

fun <T> List<T>.withAll(elements: List<T>) : List<T>{
    val copy = this.toMutableList()
    copy.addAll(elements)
    return copy
}

fun <T> List<T>.withoutAll(elements: List<T>) : List<T>{
    val copy = this.toMutableList()
    copy.removeAll(elements)
    return copy
}

fun <T> Set<T>.with(element: T) : Set<T>{
    val copy = this.toMutableSet()
    copy.add(element)
    return copy
}

fun <T> Set<T>.withAll(elements: Set<T>) : Set<T>{
    val copy = this.toMutableSet()
    copy.addAll(elements)
    return copy
}

fun <T> Set<T>.withoutAll(elements: Set<T>) : Set<T>{
    val copy = this.toMutableSet()
    copy.removeAll(elements)
    return copy
}
