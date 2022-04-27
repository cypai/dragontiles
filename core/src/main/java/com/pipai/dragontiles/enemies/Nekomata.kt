package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.*

class Nekomata : Enemy() {

    override val id: String = "base:enemies:Nekomata"
    override val assetName: String = "nekomata.png"

    override val hpMax: Int = 160
    override val fluxMax: Int = 0

    private var turns = 0

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, CursedAura(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        val attack = AttackIntent(this, 4, 2, Element.NONE)
        if (api.heroHasStatus(Overloaded::class)) {
            return attack
        }
        return when (turns % 3) {
            0 -> attack
            1 -> StrategicIntent(
                this,
                listOf(Strength(4)),
                listOf(Weak(3, true), Vulnerable(3, true)),
                listOf(
                    RandomTileStatusInflictStrategy(
                        TileStatus.CURSE,
                        4,
                        TileStatusInflictStrategy.NotEnoughStrategy.SKIP
                    )
                ),
            )
            else -> attack
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
