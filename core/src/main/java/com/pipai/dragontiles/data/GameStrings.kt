package com.pipai.dragontiles.data

import org.yaml.snakeyaml.Yaml

class GameStrings {

    var language = Language.EN
    private val strings: MutableMap<String, Map<Language, Map<String, String>>> = mutableMapOf()
    private val keywordIds: MutableMap<Language, MutableMap<String, String>> = mutableMapOf()

    private val keywordIdRegex = "\\w+:keywords:\\w+".toRegex()
    private val keywordKeyRegex = "@\\w+".toRegex()

    fun load(raw: String) {
        val map = Yaml().load<Map<String, Map<String, Map<String, String>>>>(raw)
        map.forEach { id, langs ->
            val langMap: MutableMap<Language, Map<String, String>> = mutableMapOf()
            langs.forEach { langStr, data ->
                val lang = Language.valueOf(langStr.toUpperCase())
                langMap[lang] = data
                if (keywordIdRegex.matches(id)) {
                    if (lang !in keywordIds) {
                        keywordIds[lang] = mutableMapOf()
                    }
                    keywordIds[lang]!![data["key"]!!] = id
                }
            }
            strings[id] = langMap
        }
    }

    fun spellLocalization(id: String): SpellLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return SpellLocalization(
                data["name"] ?: throw IllegalStateException("$id name not found"),
                data["description"] ?: throw IllegalStateException("$id description not found"),
                data["upgradeDescription"] ?: throw IllegalStateException("$id upgradeDescription not found"))
    }

    fun nameDescLocalization(id: String) : NameDescLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return NameDescLocalization(
                data["name"] ?: throw IllegalStateException("$id name not found"),
                data["description"] ?: throw IllegalStateException("$id description not found"))
    }

    fun keywordLocalization(id: String) : KeywordLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return KeywordLocalization(
                data["key"] ?: throw IllegalStateException("$id key not found"),
                data["name"] ?: throw IllegalStateException("$id name not found"),
                data["description"] ?: throw IllegalStateException("$id description not found"))
    }

    fun keyword(key: String): KeywordLocalization? {
        return if (key in keywordIds[language]!!) {
            keywordLocalization(keywordIds[language]!![key]!!)
        } else {
            null
        }
    }

    fun findKeywords(str: String): List<String> {
        return keywordKeyRegex.findAll(str)
                .filter { it.value in keywordIds[language]!! }
                .map { it.value }
                .toList()
    }
}

data class NameDescLocalization(val name: String, val description: String)
data class SpellLocalization(val name: String, val description: String, val upgradeDescription: String)
data class KeywordLocalization(val key: String, val name: String, val description: String)
