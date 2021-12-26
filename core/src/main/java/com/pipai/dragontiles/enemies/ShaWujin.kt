package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Sandstorm
import com.pipai.dragontiles.status.Strength

class ShaWujin : Enemy() {

    override val strId: String = "base:enemies:ShaWujin"
    override val assetName: String = "shawujin.png"

    override val hpMax: Int = 200
    override val fluxMax: Int = 60

    private var turns: Int = 1
    private var hasOverloaded = false

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Sandstorm(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        if (api.heroHasStatus(Overloaded::class) && turns % 4 != 2) {
            return AttackIntent(this, 15, 1, false, Element.ICE)
        }
        return when (turns % 4) {
            0 -> DebuffIntent(
                this, null,
                AttackIntent(this, 1, 3, false, Element.ICE),
                listOf(
                    TerminalTileStatusInflictStrategy(
                        TileStatus.FREEZE,
                        3,
                        TileStatusInflictStrategy.NotEnoughStrategy.RANDOM
                    )
                )
            )
            1 -> BuffIntent(this, Strength(3), null)
            2 -> AttackIntent(this, 30, 1, false, Element.ICE)
            else -> VentIntent(
                this, 20, Sandstorm(1)
            )
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }

    @CombatSubscribe
    fun onOverload(ev: EnemyStatusChangeEvent, api: CombatApi) {
        if (ev.enemy == this && ev.status is Overloaded) {
            hasOverloaded = true
        }
    }
}
