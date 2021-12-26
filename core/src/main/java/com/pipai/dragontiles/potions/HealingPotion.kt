package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class HealingPotion : Potion() {
    override val id: String = "base:potions:HealingPotion"
    override val assetName: String = "healing_potion.png"
    override val rarity: Rarity = Rarity.RARE
    override val type: PotionType = PotionType.UNIVERSAL
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        api.gainHpImmediate((api.runData.hero.hpMax * 0.2f).toInt())
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.healHero((api.runData.hero.hpMax * 0.2f).toInt())
    }
}
