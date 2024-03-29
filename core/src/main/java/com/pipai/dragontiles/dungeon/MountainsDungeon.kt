package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.dungeonevents.*
import com.pipai.dragontiles.enemies.*

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
                        "base:dungeons:Mountains:MoonRabbitBirds",
                        listOf(
                            Pair(MoonRabbit(), Vector2(0.5f, 4.5f)),
                            Pair(Bird(), Vector2(3f, 4.5f)),
                            Pair(Bird(), Vector2(5.5f, 4.5f)),
                        )
                    ),
                ),
                // Standard
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:KitsuneStarElemental",
                        listOf(
                            Pair(Kitsune(), Vector2(0.5f, 4.5f)),
                            Pair(StarElemental(), Vector2(4f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:WhiteDragonHorseStarElemental",
                        listOf(
                            Pair(WhiteDragonHorse(), Vector2(0.5f, 4.5f)),
                            Pair(StarElemental(), Vector2(4f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:DoubleStarElemental",
                        listOf(
                            Pair(StarElemental(), Vector2(0.5f, 4.5f)),
                            Pair(StarElemental(), Vector2(3f, 4.5f)),
                        )
                    ),
                ),
                // Elite
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:Nekomata",
                        listOf(
                            Pair(Nekomata(), Vector2(2f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:NineTailedVixen",
                        listOf(
                            Pair(NineTailedVixen(), Vector2(2f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:Yumi2",
                        listOf(
                            Pair(MoonRabbit(), Vector2(0.5f, 4.5f)),
                            Pair(Yumi2(), Vector2(4.5f, 4.5f)),
                        )
                    ),
                ),
                // Boss
                listOf(
                    Encounter(
                        "base:dungeons:Mountains:ChangE",
                        listOf(
                            Pair(ChangE(), Vector2(1f, 4.5f)),
                            Pair(Yumi3(), Vector2(4f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Mountains:TamamoNoMae",
                        listOf(
                            Pair(TamamoNoMae(), Vector2(3f, 4.5f)),
                        )
                    ),
                ),
                // Dungeon Events
                listOf(
                    FreeRelic(),
                    UnusedSeal(),
                    WhatDoesPotOfGreedDo(),
                    CheapUpgrades(),
                ),
                MountainsStartEvent(),
            )
        }
    }
}
