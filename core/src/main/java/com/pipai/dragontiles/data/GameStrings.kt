package com.pipai.dragontiles.data

import org.yaml.snakeyaml.Yaml

class GameStrings {

    var language = Language.EN
    private val strings: MutableMap<String, Map<Language, Map<String, String>>> = mutableMapOf()
    private val eventStrings: MutableMap<String, Map<String, Map<Language, Map<String, String>>>> = mutableMapOf()
    private val keywordIds: MutableMap<Language, MutableMap<String, String>> = mutableMapOf()

    private val keywordIdRegex = "\\w+:keywords:\\w+".toRegex()
    private val keywordKeyRegex = "@\\w+".toRegex()

    fun load(raw: String) {
        val map = Yaml().load<Map<String, Map<String, Map<String, String>>>>(raw)
        map.forEach { (id, langs) ->
            val langMap: MutableMap<Language, Map<String, String>> = mutableMapOf()
            langs.forEach { (langStr, data) ->
                val lang = Language.valueOf(langStr.uppercase())
                langMap[lang] = data
                if (keywordIdRegex.matches(id)) {
                    if (lang !in keywordIds) {
                        keywordIds[lang] = mutableMapOf()
                    }
                    keywordIds[lang]!![data["key"]!!] = id
                    if ("alias" in data) {
                        keywordIds[lang]!![data["alias"]!!] = id
                    }
                }
            }
            strings[id] = langMap
        }
    }

    fun loadEvent(raw: String) {
        val map = Yaml().load<Map<String, Map<String, Map<String, Map<String, String>>>>>(raw)
        map.forEach { (eventId, eventMap) ->
            val parsedEventMap: MutableMap<String, MutableMap<Language, Map<String, String>>> = mutableMapOf()
            eventMap.forEach { (eventKey, langData) ->
                val parsedLangMap: MutableMap<Language, Map<String, String>> = mutableMapOf()
                langData.forEach { (langStr, data) ->
                    val lang = Language.valueOf(langStr.uppercase())
                    parsedLangMap[lang] = data
                }
                parsedEventMap[eventKey] = parsedLangMap
            }
            eventStrings[eventId] = parsedEventMap
        }
    }

    fun textLocalization(localized: Localized): String {
        val id = localized.id
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return data["text"] ?: throw IllegalStateException("$id text not found")
    }

    fun nameLocalization(localized: Localized): String {
        val id = localized.id
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return data["name"] ?: throw IllegalStateException("$id name not found")
    }

    fun nameDescLocalization(localized: Localized): NameDescLocalization {
        val id = localized.id
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return NameDescLocalization(
            data["name"] ?: throw IllegalStateException("$id name not found"),
            data["description"] ?: throw IllegalStateException("$id description not found"),
        )
    }

    fun spellLocalization(id: String): SpellLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return SpellLocalization(
            data["name"] ?: throw IllegalStateException("$id name not found"),
            data["description"] ?: throw IllegalStateException("$id description not found")
        )
    }

    fun nameDescLocalization(id: String): NameDescLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return NameDescLocalization(
            data["name"] ?: throw IllegalStateException("$id name not found"),
            data["description"] ?: throw IllegalStateException("$id description not found")
        )
    }

    fun keywordLocalization(id: String): KeywordLocalization {
        val data = strings[id]?.get(language) ?: throw IllegalArgumentException("$id not found")
        return KeywordLocalization(
            data["key"] ?: throw IllegalStateException("$id key not found"),
            data["name"] ?: throw IllegalStateException("$id name not found"),
            data["description"] ?: throw IllegalStateException("$id description not found")
        )
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

    fun eventLocalization(id: String): EventLocalization {
        val event = eventStrings[id]!!
        val keyText = event.mapValues { it.value[language]?.get("text") ?: "" }.toMutableMap()
        val name = keyText.remove("name")!!
        return EventLocalization(name, keyText)
    }
}

data class NameDescLocalization(val name: String, val description: String)
data class SpellLocalization(val name: String, val description: String)
data class KeywordLocalization(val key: String, val name: String, val description: String)
data class EventLocalization(val name: String, val keyText: Map<String, String>)
