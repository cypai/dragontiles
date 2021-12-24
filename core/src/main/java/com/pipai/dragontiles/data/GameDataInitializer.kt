package com.pipai.dragontiles.data

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.dungeon.Dungeon
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.dungeonevents.*
import com.pipai.dragontiles.enemies.*
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.potions.ExplosivePotion
import com.pipai.dragontiles.potions.HealingPotion
import com.pipai.dragontiles.relics.*
import com.pipai.dragontiles.spells.colorless.*
import com.pipai.dragontiles.spells.elementalist.Split
import com.pipai.dragontiles.spells.upgrades.EfficiencyUpgrade
import com.pipai.dragontiles.spells.upgrades.PowerUpgrade
import com.pipai.dragontiles.spells.upgrades.SurgeUpgrade

class GameDataInitializer {

    fun init(gameData: GameData) {
        initHeroClasses(gameData)
        initRelics(gameData)
        initSpells(gameData)
        initSpellUpgrades(gameData)
        initPotions(gameData)
        initPlainsDungeon(gameData)
    }

    private fun initHeroClasses(gameData: GameData) {
        gameData.addHeroClass(Elementalist())
    }

    private fun initRelics(gameData: GameData) {
        gameData.addRelic(Bamboo())
        gameData.addRelic(Cherry())
        gameData.addRelic(Coffee())
        gameData.addRelic(Elixir())
        gameData.addRelic(Ginsengfruit())
        gameData.addRelic(Peach())
        gameData.addRelic(RabbitsFoot())
        gameData.addRelic(Tea())
        gameData.addRelic(Transmuter())
    }

    private fun initSpells(gameData: GameData) {
        gameData.allHeroClasses().forEach { initHeroSpells(gameData, it.id) }
        gameData.addSpell(GameData.COLORLESS, Bump())
        gameData.addSpell(GameData.COLORLESS, Nudge())
        gameData.addSpell(GameData.COLORLESS, Mulligan())
        gameData.addSpell(GameData.COLORLESS, Fetch())
        gameData.addSpell(GameData.COLORLESS, Ground())
        gameData.addSpell(GameData.COLORLESS, Reserve())
        gameData.addSpell(GameData.COLORLESS, Split())
        gameData.addSpell(GameData.COLORLESS, DragonRage())
        gameData.addSpell(GameData.COLORLESS, DragonScale())
    }

    private fun initSpellUpgrades(gameData: GameData) {
        gameData.addSpellUpgrade(PowerUpgrade())
        gameData.addSpellUpgrade(SurgeUpgrade())
        gameData.addSpellUpgrade(EfficiencyUpgrade())
    }

    private fun initHeroSpells(gameData: GameData, heroClassId: String) {
        val heroClass = gameData.getHeroClass(heroClassId)
        heroClass.starterDeck.forEach {
            gameData.addSpell(heroClassId, it)
        }
        heroClass.spells.forEach {
            gameData.addSpell(heroClassId, it)
        }
    }

    private fun initPotions(gameData: GameData) {
        gameData.addPotion(ExplosivePotion())
        gameData.addPotion(HealingPotion())
    }

    private fun initPlainsDungeon(gameData: GameData) {
        val dungeon = Dungeon(
            "base:dungeons:Plains",
            // Easy
            listOf(
                Encounter(
                    "base:dungeons:Plains:LargeTurtle",
                    listOf(
                        Pair(LargeTurtle(), Vector2(750f, 420f))
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
                        Pair(LargeTurtle(), Vector2(650f, 420f)),
                        Pair(Slime(), Vector2(1000f, 420f))
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
                    listOf(Pair(Bull(), Vector2(750f, 420f)))),
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
                        Pair(Minotaur(), Vector2(750f, 420f))
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
        gameData.addDungeon(dungeon)
    }
}
