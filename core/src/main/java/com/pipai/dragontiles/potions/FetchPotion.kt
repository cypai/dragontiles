package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class FetchPotion : Potion() {
    override val id: String = "base:potions:FetchPotion"
    override val assetName: String = "fetch_potion.png"
    override val rarity: Rarity = Rarity.UNCOMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.fetch()
    }
}
