package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.Immortality
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Strength

class TamamoNoMae : Enemy() {

    override val id: String = "base:enemies:TamamoNoMae"
    override val assetName: String = "tamamo_no_mae.png"

    override val hpMax: Int = 90
    override val fluxMax: Int = 180

    private var turns: Int = 0

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Immortality(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        val tileStatus = when (turns % 5) {
            0 -> TileStatus.VOLATILE
            1 -> TileStatus.BURN
            2 -> TileStatus.FREEZE
            3 -> TileStatus.SHOCK
            else -> TileStatus.CURSE // will be unused
        }
        return if (turns % 5 < 4) {
            DebuffIntent(
                this,
                listOf(),
                listOf(
                    RandomTileStatusInflictStrategy(
                        tileStatus,
                        9,
                    )
                ),
                AttackIntent(this, 9, 1, Element.NONE),
            )
        } else {
            BuffIntent(
                this,
                listOf(Strength(3)),
                AttackIntent(this, 6, 9, Element.NONE),
            )
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
