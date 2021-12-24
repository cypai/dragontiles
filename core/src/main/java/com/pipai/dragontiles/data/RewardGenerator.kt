package com.pipai.dragontiles.data

import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.choose

class RewardGenerator {

    fun generate(gameData: GameData, runData: RunData, rewardConfig: CombatRewardConfig){
        val actualRewards = mutableListOf<Reward>()
        val heroClass = gameData.getHeroClass(runData.hero.heroClassId)
        val rng = runData.seed.rewardRng()
        actualRewards.add(Reward.SpellDraftReward(heroClass.getRandomClassSpells(runData.seed, 3).map { it.toInstance() }))
        actualRewards.add(Reward.GoldReward(rewardConfig.gold))
        if (rng.nextFloat() < rewardConfig.potionChance) {
            actualRewards.add(Reward.PotionReward(gameData.allPotions().choose(rng).id))
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
}
