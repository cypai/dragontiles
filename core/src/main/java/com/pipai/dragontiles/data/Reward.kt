package com.pipai.dragontiles.data

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.SpellInstance

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class Reward {
    data class SpellDraftReward(val spells: List<SpellInstance>) : Reward()
    data class GoldReward(val amount: Int) : Reward()
    data class RelicReward(val relic: RelicInstance) : Reward()
    data class PotionReward(val potion: String) : Reward()
    class SideboardSpaceReward : Reward()
    class EmptyReward : Reward()
}
