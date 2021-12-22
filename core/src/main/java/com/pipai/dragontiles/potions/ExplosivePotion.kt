package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.utils.getLogger

class ExplosivePotion : Potion() {
    private val logger = getLogger()
    override val strId: String = "base:potions:ExplosivePotion"
    override val assetName: String = "explosive_potion.png"
    override val targetType: PotionTargetType = PotionTargetType.ENEMY

    override suspend fun onUse(target: Int?, api: CombatApi) {
        if (target == null) {
            logger.error("Attempted to use Explosive Potion without a target")
        } else {
            val enemy = api.getEnemy(target)
            api.attack(enemy, Element.NONE, 10, asAttack = false)
        }
    }
}
