package com.pipai.dragontiles.data

class Keywords {

    private val keywords: MutableMap<String, KeywordData> = mutableMapOf()
    private val regex = "@\\w+".toRegex()

    init {
        keywords["@Elemented"] = KeywordData("Elemented",
                "The element of the @Components determines the damage element. Star or Life tiles use the Non-Elemental element.")
        keywords["@Components"] = KeywordData("Components",
                "The tiles used to activate the spell.")
        keywords["@Repeatable"] = KeywordData("Repeatable",
                "Can be casted multiple times per turn, or unlimited times if no number is provided.")
        keywords["@OpenDiscard"] = KeywordData("Open Discard",
                "Place the tile into the Open Discard pool.")
        keywords["@Numeric"] = KeywordData("Numeric",
                "Uses the highest numeric value of the @Components.")
        keywords["@Exhaust"] = KeywordData("Exhaust",
                "Can only be cast once per combat.")
        keywords["@ElementalTile"] = KeywordData("Elemental Tile",
                "Fire, Ice, or Lightning tile.")
        keywords["@NonElementalTile"] = KeywordData("Non-Elemental Tile",
                "Star or Life tile.")
        keywords["@Break"] = KeywordData("Break",
                "Takes twice the damage when attacked with the broken element.")
        keywords["@Power"] = KeywordData("Power",
                "Attacks deal 1 more damage per point of Power.")
        keywords["@Defense"] = KeywordData("Defense",
                "Damage taken via attacks decreased by 1 per point of Defense.")
        keywords["@AttackPower"] = KeywordData("Attack Power",
                "The base damage of the spell. Can be decreased by attacking the spell.")
        keywords["@EffectPower"] = KeywordData("Effect Power",
                "Can be decreased by attacking the spell when Attack Power is 0.")
        keywords["@Disrupt"] = KeywordData("Disrupt",
                "When a spell's Attack Power and Effect Power is 0, the spell is Disrupted and is destroyed.")
    }

    fun checkKeywords(str: String): List<KeywordData> {
        val list: MutableList<KeywordData> = mutableListOf()
        var currentStr = str
        var done = false
        while (!done) {
            done = true
            val tokens = currentStr.replace(".", "").split(" ")
            currentStr = ""
            tokens.forEach {
                if (it in keywords) {
                    val data = keywords[it]!!
                    if (data !in list) {
                        list.add(data)
                        done = false
                        currentStr += data.description
                    }
                }
            }
        }
        return list
    }

}

data class KeywordData(val text: String, val description: String)
