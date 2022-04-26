package com.pipai.dragontiles.data

import com.pipai.dragontiles.dungeon.PlainsDungeon
import com.pipai.dragontiles.hero.Elementalist
import com.pipai.dragontiles.potions.*
import com.pipai.dragontiles.relics.*
import com.pipai.dragontiles.spells.colorless.*
import com.pipai.dragontiles.spells.elementalist.Split
import com.pipai.dragontiles.spells.upgrades.*

class GameDataInitializer {

    fun init(gameData: GameData) {
        initHeroClasses(gameData)
        initRelics(gameData)
        initSpells(gameData)
        initSpellUpgrades(gameData)
        initPotions(gameData)
        gameData.addDungeon(PlainsDungeon.create())
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
        gameData.addRelic(Gourd())
        gameData.addRelic(MortarAndPestle())
        gameData.addRelic(FireFlower())
        gameData.addRelic(PerfectFreeze())
        gameData.addRelic(LightningRod())
        gameData.addRelic(StoneEgg())
        gameData.addRelic(Nunchaku())
        gameData.addRelic(StockOption())
        gameData.addRelic(Inkstone())
        gameData.addRelic(YinYangOrb())
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
        gameData.addSpell(GameData.COLORLESS, Pong())
        gameData.addSpell(GameData.COLORLESS, Chow())
        gameData.addSpell(GameData.COLORLESS, PingHu())
        gameData.addSpell(GameData.COLORLESS, DuiDuiHu())
        gameData.addSpell(GameData.COLORLESS, DragonRage())
        gameData.addSpell(GameData.COLORLESS, DragonScale())
        gameData.addSpell(GameData.COLORLESS, PotOfGreed())
        gameData.addSpell(GameData.COLORLESS, Patience())
    }

    private fun initSpellUpgrades(gameData: GameData) {
        gameData.addSpellUpgrade(PowerUpgrade())
        gameData.addSpellUpgrade(SurgeUpgrade())
        gameData.addSpellUpgrade(EfficiencyUpgrade())
        gameData.addSpellUpgrade(HeatsinkUpgrade())
        gameData.addSpellUpgrade(AntifreezeUpgrade())
        gameData.addSpellUpgrade(GroundwireUpgrade())
        gameData.addSpellUpgrade(RepeatUpgrade())
        gameData.addSpellUpgrade(EternalUpgrade())
        gameData.addSpellUpgrade(DoublestackUpgrade())
        gameData.addSpellUpgrade(XUpgrade())
        gameData.addSpellUpgrade(FetchUpgrade())
        gameData.addSpellUpgrade(SwapUpgrade())
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
        gameData.addPotion(BlastPotion())
        gameData.addPotion(HealingPotion())
        gameData.addPotion(StrengthPotion())
        gameData.addPotion(WeakPotion())
        gameData.addPotion(VentingPotion())
        gameData.addPotion(HastePotion())
        gameData.addPotion(SwapPotion())
        gameData.addPotion(FetchPotion())
        gameData.addPotion(FireCrystal())
        gameData.addPotion(IceCrystal())
        gameData.addPotion(LightningCrystal())
        gameData.addPotion(LifeCrystal())
        gameData.addPotion(StarCrystal())
    }

}
