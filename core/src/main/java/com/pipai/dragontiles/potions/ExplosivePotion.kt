package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.utils.getLogger

class ExplosivePotion : Potion() {
    private val logger = getLogger()
    override val id: String = "base:potions:ExplosivePotion"
    override val assetName: String = "explosive_potion.png"
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.ENEMY

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        if (target == null) {
            logger.error("Attempted to use Explosive Potion without a target")
        } else {
            val enemy = api.getEnemy(target)
            api.attack(enemy, Element.NONE, 10, asAttack = false, flags = listOf())
        }
    }
}
