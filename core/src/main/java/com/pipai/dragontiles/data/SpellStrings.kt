package com.pipai.dragontiles.data

import org.yaml.snakeyaml.Yaml

class SpellStrings {

    var language = Language.EN
    private val strings: MutableMap<String, Map<Language, SpellString>> = mutableMapOf()

    fun load(raw: String) {
        val map = Yaml().load<Map<String, Map<String, Map<String, String>>>>(raw)
        map.forEach { id, langs ->
            val langMap: MutableMap<Language, SpellString> = mutableMapOf()
            langs.forEach { lang, data ->
                langMap[Language.valueOf(lang.toUpperCase())] = SpellString(
                        data["name"]!!,
                        data["description"]!!,
                        data["upgradeDescription"] ?: data["description"]!!)
            }
            strings[id] = langMap
        }
    }

    fun all(id: String) = strings[id]!![language]!!

    fun name(id: String) = strings[id]!![language]!!.name

    fun description(id: String) = strings[id]!![language]!!.description

    fun upgradeDescription(id: String) = strings[id]!![language]!!.upgradeDescription
}

data class SpellString(val name: String, val description: String, val upgradeDescription: String)
