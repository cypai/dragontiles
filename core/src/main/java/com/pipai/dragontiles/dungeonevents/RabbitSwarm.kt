package com.pipai.dragontiles.dungeonevents

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.combat.SpellRewardType
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.enemies.KillerRabbit
import com.pipai.dragontiles.enemies.Rat
import com.pipai.dragontiles.relics.RabbitsFoot

class RabbitSwarm : DungeonEvent() {

    override val id = "base:events:RabbitSwarm"
    override val beginningTextId = "start"
    override val beginningOptions: List<EventOption> = listOf(FightOption(), SkipOption())

    override fun available(floorNumber: Int): Boolean {
        return floorNumber > 5
    }

    private class FightOption : EventOption {
        override val id = "fight"

        override fun onSelect(api: EventApi) {
            val encounter = Encounter(
                "base:dungeons:Plains:RabbitSwarm",
                listOf(
                    Pair(KillerRabbit(), Vector2(0.5f, 4.5f)),
                    Pair(KillerRabbit(), Vector2(3f, 4.5f)),
                    Pair(KillerRabbit(), Vector2(5.5f, 4.5f)),
                )
            )
            api.startCombat(encounter, CombatRewardConfig(SpellRewardType.STANDARD, 3, false, RabbitsFoot(), api.runData.potionChance))
        }
    }

    private class SkipOption : EventOption {
        override val id = "skip"

        override fun onSelect(api: EventApi) {
            api.allowMapAdvance()
            api.showMap()
        }
    }
}
