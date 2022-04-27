package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.dungeonevents.FreeRelic
import com.pipai.dragontiles.dungeonevents.MountainsStartEvent
import com.pipai.dragontiles.dungeonevents.UnusedSeal
import com.pipai.dragontiles.dungeonevents.WhatDoesPotOfGreedDo
import com.pipai.dragontiles.enemies.AncientTurtle
import com.pipai.dragontiles.enemies.Nekomata
import com.pipai.dragontiles.enemies.ShaWujin
import com.pipai.dragontiles.enemies.Yumi

class MountainsDungeon {
    companion object {
        fun create(): Dungeon {
            return Dungeon(
                "base:dungeons:Mountains",
                // Easy
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:AncientTurtle",
                        listOf(
                            Pair(AncientTurtle(), Vector2(2f, 4.5f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:AncientTurtle2",
                        listOf(
                            Pair(AncientTurtle(), Vector2(2f, 4.5f))
                        )
                    ),
                ),
                // Standard
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:AncientTurtle3",
                        listOf(
                            Pair(AncientTurtle(), Vector2(2f, 4.5f))
                        )
                    ),
                ),
                // Elite
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:Nekomata",
                        listOf(
                            Pair(Nekomata(), Vector2(2f, 4.5f))
                        )
                    ),
                ),
                // Boss
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:ShaWujin",
                        listOf(
                            Pair(ShaWujin(), Vector2(2f, 4.5f))
                        )
                    ),
                ),
                // Dungeon Events
                listOf(
                    FreeRelic(),
                    UnusedSeal(),
                    WhatDoesPotOfGreedDo(),
                ),
                MountainsStartEvent(),
            )
        }
    }
}
