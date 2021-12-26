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
                            Pair(LargeTurtle(), Vector2(750f, 400f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:SlimePair",
                        listOf(
                            Pair(Slime(), Vector2(740f, 430f)),
                            Pair(Slime(), Vector2(910f, 430f))
                        )
                    ),
                ),
                // Standard
                listOf(
                    Encounter(
                        "base:dungeons:Plains:TurtleAndSlime",
                        listOf(
                            Pair(LargeTurtle(), Vector2(650f, 400f)),
                            Pair(Slime(), Vector2(1000f, 400f))
                        ),
                    ),
                    Encounter(
                        "base:dungeons:Plains:KillerRabbitPair",
                        listOf(
                            Pair(KillerRabbit(), Vector2(740f, 430f)),
                            Pair(KillerRabbit(), Vector2(1010f, 430f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:Bull",
                        listOf(Pair(Bull(), Vector2(750f, 400f)))
                    ),
                    Encounter(
                        "base:dungeons:Plains:Rats",
                        listOf(
                            Pair(Rat(), Vector2(740f, 400f)),
                            Pair(Rat(), Vector2(1010f, 500f)),
                            Pair(Rat(), Vector2(1010f, 280f)),
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:RiverSpiritAndSlime",
                        listOf(
                            Pair(RiverSpirit(), Vector2(740f, 430f)),
                            Pair(Slime(), Vector2(1010f, 430f))
                        )
                    ),
                ),
                // Elite
                listOf(
                    Encounter(
                        "base:dungeons:Plains:Yumi",
                        listOf(
                            Pair(Yumi(), Vector2(750f, 420f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:Minotaur",
                        listOf(
                            Pair(Minotaur(), Vector2(750f, 400f))
                        )
                    ),
                    Encounter(
                        "base:dungeons:Plains:DragonHorseTrio",
                        listOf(
                            Pair(FlameDragonHorse(), Vector2(740f, 400f)),
                            Pair(RiverDragonHorse(), Vector2(1010f, 500f)),
                            Pair(WhiteDragonHorse(), Vector2(1010f, 280f)),
                        )
                    ),
                ),
                // Boss
                listOf(
                    Encounter(
                        "base:dungeons:Plains:ShaWujin",
                        listOf(
                            Pair(ShaWujin(), Vector2(750f, 400f))
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
                ),
                PlainsStartEvent(),
            )
        }
    }
}
