package com.pipai.dragontiles.utils

inline fun <reified T : Enum<T>> valueOfOrDefault(type: String, default: T): T {
    try {
        return enumValueOf(type)
    } catch (e: IllegalArgumentException) {
        return default
    }
}
