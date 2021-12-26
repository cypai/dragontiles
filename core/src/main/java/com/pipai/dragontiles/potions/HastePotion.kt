package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Dodge

class HastePotion : Potion() {
    override val id: String = "base:potions:HastePotion"
    override val assetName: String = "haste_potion.png"
    override val rarity: Rarity = Rarity.RARE
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.addStatusToHero(Dodge(1))
    }
}
