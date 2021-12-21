package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus

class FlameDragonHorse : Enemy() {

    override val strId: String = "base:enemies:FlameDragonHorse"
    override val assetName: String = "flame_horse.png"

    override val hpMax: Int = 20
    override val fluxMax: Int = 25

    private var flag = true

    override fun getIntent(): Intent {
        return if (flag) {
            DebuffIntent(this, null, null, listOf(RandomTileStatusInflictStrategy(TileStatus.BURN, 3)))
        } else {
            AttackIntent(this, 10, 1, false, Element.FIRE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent()
    }
}