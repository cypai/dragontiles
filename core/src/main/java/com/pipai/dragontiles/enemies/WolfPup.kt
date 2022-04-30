package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.artemis.systems.animation.TalkAnimation
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.AssetConfig
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.StringLocalized
import com.pipai.dragontiles.status.Strength

class WolfPup : Enemy() {

    override val id: String = "base:enemies:WolfPup"
    override val assetName: String = "wolf.png"
    override val assetConfig: AssetConfig = AssetConfig(AssetType.SPRITE, 1.5f)

    override val hpMax: Int = 20
    override val fluxMax: Int = 15

    override fun getIntent(api: CombatApi): Intent {
        return BuffIntent(
            this,
            listOf(),
            null,
            {
                api.animate(TalkAnimation(Combatant.EnemyCombatant(this), StringLocalized("base:text:Howl")))
                api.addAoeStatus(Strength(2))
            }
        )
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent(api)
    }
}
