package com.pipai.dragontiles.data

import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.combat.SpellRewardType
import com.pipai.dragontiles.hero.HeroClass
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.choose
import com.pipai.dragontiles.utils.withoutAll
import kotlin.random.Random

class RewardGenerator {

    fun generate(gameData: GameData, runData: RunData, rewardConfig: CombatRewardConfig){
        val actualRewards = mutableListOf<Reward>()
        val heroClass = gameData.getHeroClass(runData.hero.heroClassId)
        val rng = runData.seed.rewardRng()
        actualRewards.add(Reward.SpellDraftReward(generateCardRewards(heroClass, rewardConfig.spellRewardType, rng, 3)))
        actualRewards.add(Reward.GoldReward(rewardConfig.gold))
        if (rng.nextFloat() < rewardConfig.potionChance) {
            actualRewards.add(Reward.PotionReward(choosePotion(gameData, rng).id))
            runData.potionChance = GameData.BASE_POTION_CHANCE
        } else {
            runData.potionChance += 0.1f
        }
        if (rewardConfig.randomRelic) {
            actualRewards.add(Reward.RelicReward(RelicInstance(runData.availableRelics.choose(rng), 0)))
        }
        if (rewardConfig.relic != null) {
            actualRewards.add(Reward.RelicReward(rewardConfig.relic.toInstance()))
        }
        runData.combatRewards.clear()
        runData.combatRewards.addAll(actualRewards)
    }

    fun generateCardRewards(heroClass: HeroClass, spellRewardType: SpellRewardType, rng: Random, amount: Int): List<SpellInstance> {
        val rewards: MutableList<SpellInstance> = mutableListOf()
        repeat(amount) {
            var rare = 0f
            var uncommon = 0f
            when (spellRewardType) {
                SpellRewardType.STANDARD -> {
                    rare = 0.05f
                    uncommon = 0.4f
                }
                SpellRewardType.ELITE -> {
                    rare = 0.1f
                    uncommon = 0.5f
                }
                SpellRewardType.BOSS -> {
                    rare = 1f
                }
            }
            val n = rng.nextFloat()
            val rarityRoll = when {
                n < rare -> Rarity.RARE
                n >= rare && n < uncommon -> Rarity.UNCOMMON
                else -> Rarity.COMMON
            }
            val chosen = heroClass.spells
                .filter { it.rarity == rarityRoll }
                .map { it.toInstance() }
                .withoutAll(rewards)
                .choose(rng)
            rewards.add(chosen)
        }
        return rewards
    }

    fun choosePotion(gameData: GameData, rng: Random): Potion {
        val n = rng.nextFloat()
        val rarityRoll = when  {
            n < 0.65f -> {
                Rarity.COMMON
            }
            n in 0.65f..0.9f -> {
                Rarity.UNCOMMON
            }
            else -> {
                Rarity.RARE
            }
        }
        return gameData.allPotions().filter { it.rarity == rarityRoll }.choose(rng)
    }
}
