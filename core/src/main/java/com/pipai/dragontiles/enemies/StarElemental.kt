package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus

class StarElemental : Enemy() {

    override val id: String = "base:enemies:StarElemental"
    override val assetName: String = "star_elemental.png"

    override val hpMax: Int = 16
    override val fluxMax: Int = 80

    private var n = 3
    private var turns: Int = 0

    override fun getIntent(api: CombatApi): Intent {
        return if (turns % 3 < 2) {
            DebuffIntent(
                this, listOf(), listOf(
                    NonorphanedTileStatusInflictStrategy(
                        TileStatus.VOLATILE,
                        n,
                    ),
                ),
                null
            )
        } else {
            val volatility = api.combat.hand.filter { it.tileStatus == TileStatus.VOLATILE }.size
            AttackIntent(this, 4, volatility, Element.NONE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        n++
        return getIntent(api)
    }
}
