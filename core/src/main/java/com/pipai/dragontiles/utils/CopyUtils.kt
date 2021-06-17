package com.pipai.dragontiles.utils

interface ShallowCopyable<T : ShallowCopyable<T>> {
    fun shallowCopy(): T
}

interface DeepCopyable<T : DeepCopyable<T>> {
    fun deepCopy(): T
}

fun <T : DeepCopyable<T>> deepCopy(list: List<T>): List<T> {
    return list.map { it.deepCopy() }
}
