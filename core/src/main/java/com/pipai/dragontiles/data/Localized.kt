package com.pipai.dragontiles.data

interface Localized {
    val id: String

    fun additionalKeywords(): List<String> = listOf()
    fun additionalLocalized(): List<String> = listOf()
}

data class StringLocalized(override val id: String) : Localized
