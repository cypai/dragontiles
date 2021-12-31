package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus

class FlameDragonHorse : Enemy() {

    override val id: String = "base:enemies:FlameDragonHorse"
    override val assetName: String = "flame_horse.png"

    override val hpMax: Int = 20
    override val fluxMax: Int = 25

    private var flag = true

    override fun getIntent(api: CombatApi): Intent {
        return if (flag) {
            DebuffIntent(
                this,
                null,
                null,
                listOf(
                    RandomTileStatusInflictStrategy(
                        TileStatus.BURN,
                        3,
                        TileStatusInflictStrategy.NotEnoughStrategy.SKIP
                    )
                )
            )
        } else {
            AttackIntent(this, 10, 1, Element.FIRE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent(api)
    }
}
