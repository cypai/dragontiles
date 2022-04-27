package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.status.Vulnerable
import com.pipai.dragontiles.status.Weak

class Kitsune : Enemy() {

    override val id: String = "base:enemies:Kitsune"
    override val assetName: String = "kitsune.png"

    override val hpMax: Int = 18
    override val fluxMax: Int = 45

    private var turns: Int = 0

    override fun getIntent(api: CombatApi): Intent {
        return when (turns % 3) {
            0 -> AttackIntent(this, 9, 1, Element.NONE)
            1 -> BuffIntent(this, listOf(Weak(3, false), Vulnerable(2, false)), null)
            else -> StrategicIntent(
                this,
                listOf(Strength(3)),
                listOf(),
                listOf(
                    NonterminalTileStatusInflictStrategy(
                        TileStatus.CURSE,
                        1,
                        TileStatusInflictStrategy.NotEnoughStrategy.SKIP
                    ),
                    NonterminalTileStatusInflictStrategy(
                        TileStatus.VOLATILE,
                        1,
                        TileStatusInflictStrategy.NotEnoughStrategy.SKIP
                    ),
                )
            )
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
