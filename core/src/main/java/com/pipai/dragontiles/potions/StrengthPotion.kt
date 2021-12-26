package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Strength

class StrengthPotion : Potion() {
    override val id: String = "base:potions:StrengthPotion"
    override val assetName: String = "strength_potion.png"
    override val rarity: Rarity = Rarity.COMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.addStatusToHero(Strength(2))
    }
}
