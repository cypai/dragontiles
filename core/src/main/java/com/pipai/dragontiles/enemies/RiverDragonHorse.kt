package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus

class RiverDragonHorse : Enemy() {

    override val id: String = "base:enemies:RiverDragonHorse"
    override val assetName: String = "river_horse.png"

    override val hpMax: Int = 20
    override val fluxMax: Int = 25

    private var flag = true

    override fun getIntent(api: CombatApi): Intent {
        return if (flag) {
            AttackIntent(this, 10, 1, Element.ICE)
        } else {
            DebuffIntent(
                this,
                listOf(),
                listOf(
                    RandomTileStatusInflictStrategy(
                        TileStatus.FREEZE,
                        3
                    )
                ),
                null
            )
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent(api)
    }
}
