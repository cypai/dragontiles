package com.pipai.dragontiles.dungeonevents

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.combat.CombatRewards
import com.pipai.dragontiles.combat.SpellRewardType
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.enemies.KillerRabbit
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
                listOf(
                    Pair(KillerRabbit(), Vector2(740f, 400f)),
                    Pair(KillerRabbit(), Vector2(1010f, 500f)),
                    Pair(KillerRabbit(), Vector2(1010f, 280f)),
                )
            )
            api.startCombat(encounter, CombatRewards(SpellRewardType.STANDARD, 3, false, RabbitsFoot()))
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
