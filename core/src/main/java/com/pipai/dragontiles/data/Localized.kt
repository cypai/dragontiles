package com.pipai.dragontiles.data

interface Localized {
    val id: String

    fun additionalKeywords(): List<String> = listOf()
    fun additionalLocalized(): List<String> = listOf()
}

@JvmInline
value class StringLocalized(override val id: String) : Localized
