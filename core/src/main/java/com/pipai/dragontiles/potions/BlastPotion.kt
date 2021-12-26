package com.pipai.dragontiles.potions

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.spells.Rarity

class BlastPotion : Potion() {
    override val id: String = "base:potions:BlastPotion"
    override val assetName: String = "blast_potion.png"
    override val rarity: Rarity = Rarity.COMMON
    override val type: PotionType = PotionType.COMBAT_ONLY
    override val targetType: PotionTargetType = PotionTargetType.ENEMY

    override fun onNonCombatUse(api: GlobalApi) {
        throw NotImplementedError()
    }

    override suspend fun onCombatUse(target: Int?, api: CombatApi) {
        api.aoeAttack(Element.NONE, 5, flags = listOf())
    }
}
