package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.utils.getLogger

class HealingPotion : Potion() {
    override val strId: String = "base:potions:HealingPotion"
    override val assetName: String = "healing_potion.png"
    override val type: PotionType = PotionType.UNIVERSAL
    override val targetType: PotionTargetType = PotionTargetType.NONE

    override fun onNonCombatUse(api: GlobalApi) {
        api.gainHpImmediate((api.runData.hero.hpMax * 0.2f).toInt())
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.healHero((api.runData.hero.hpMax * 0.2f).toInt())
    }
}
