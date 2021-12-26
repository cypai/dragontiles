package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.status.Weak
import com.pipai.dragontiles.utils.getLogger

class WeakPotion : Potion() {
    private val logger = getLogger()
    override val id: String = "base:potions:WeakPotion"
    override val assetName: String = "weak_potion.png"
    override val rarity: Rarity = Rarity.COMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.ENEMY

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        if (target == null) {
            logger.error("Attempted to use Weak Potion without a target")
        } else {
            val enemy = api.getEnemy(target)
            api.addStatusToEnemy(enemy, Weak(3, false))
        }
    }
}
