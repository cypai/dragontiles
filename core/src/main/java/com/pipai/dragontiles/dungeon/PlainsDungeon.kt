package com.pipai.dragontiles.dungeon

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.dungeonevents.*
import com.pipai.dragontiles.enemies.*

class PlainsDungeon {
    companion object {
        fun create(): Dungeon {
            return Dungeon(
                "base:dungeons:Plains",
                // Easy
                listOf(
                    Encounter(
                        "base:dungeons:Plains:LargeTurtle",
                        listOf(
                            Pair(LargeTurtle(), Vector2(2f, 4.5f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:SlimePair",
                        listOf(
                            Pair(Slime(), Vector2(2f, 4.5f)),
                            Pair(Slime(), Vector2(5f, 4.5f))
                        )
                    ),
                ),
                // Standard
                listOf(
                    Encounter(
                        "base:dungeons:Plains:TurtleAndSlime",
                        listOf(
                            Pair(LargeTurtle(), Vector2(0.5f, 4.5f)),
                            Pair(Slime(), Vector2(5f, 4.5f))
                        ),
                    ),
                    Encounter(
                        "base:dungeons:Plains:KillerRabbitPair",
                        listOf(
                            Pair(KillerRabbit(), Vector2(0.5f, 4.5f)),
                            Pair(KillerRabbit(), Vector2(3f, 4.5f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:Bull",
                        listOf(Pair(Bull(), Vector2(2f, 4.5f)))
                    ),
                    Encounter(
                        "base:dungeons:Plains:Rats",
                        listOf(
                            Pair(Rat(), Vector2(0.5f, 4.5f)),
                            Pair(Rat(), Vector2(3f, 4.5f)),
                            Pair(Rat(), Vector2(5.5f, 4.5f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:RiverSpiritAndSlime",
                        listOf(
                            Pair(RiverSpirit(), Vector2(1f, 4.5f)),
                            Pair(Slime(), Vector2(4f, 4.5f))
                        )
                    ),
                ),
                // Elite
                listOf(
                    Encounter(
                        "base:dungeons:Plains:Yumi",
                        listOf(
                            Pair(Yumi(), Vector2(2f, 4.5f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:Minotaur",
                        listOf(
                            Pair(Minotaur(), Vector2(2f, 4.5f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:DragonHorseTrio",
                        listOf(
                            Pair(FlameDragonHorse(), Vector2(0.5f, 4.5f)),
                            Pair(RiverDragonHorse(), Vector2(3f, 4.5f)),
                            Pair(WhiteDragonHorse(), Vector2(5.5f, 4.5f)),
                        )
                    ),
                ),
                // Boss
                listOf(
                    Encounter(
                        "base:dungeons:Plains:ShaWujin",
                        listOf(
                            Pair(ShaWujin(), Vector2(2f, 4.5f))
                        )
                    ),
                ),
                // Dungeon Events
                listOf(
                    ThornedBush(),
                    FreeRelic(),
                    UnusedSeal(),
                    ShinyInAHole(),
                    RabbitSwarm(),
                    TheBeggar(),
                    StrangeLotus(),
                    StrangeScribe(),
                    WhatDoesPotOfGreedDo(),
                ),
                PlainsStartEvent(),
            )
        }
    }
}
