package com.pipai.dragontiles.combat

import com.pipai.dragontiles.dungeon.PlainsDungeon
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Spell
import java.util.*

fun runDataFixture(spells: MutableList<Spell>, relics: MutableList<Relic>): RunData {
    return RunData(
            Random(),
            Hero("Elementalist", 80, 80, 15, spells, relics),
            PlainsDungeon())
}
