package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Sandstorm
import com.pipai.dragontiles.status.Strength
import com.pipai.dragontiles.utils.chooseAmount

class ShaWujin : Enemy() {

    override val id: String = "base:enemies:ShaWujin"
    override val assetName: String = "shawujin.png"

    override val hpMax: Int = 120
    override val fluxMax: Int = 60

    private var turns: Int = 0
    private var justOverloaded = false

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Sandstorm(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        val originalIntent = when (turns % 4) {
            0 -> BuffIntent(this, listOf(Strength(3)), null)
            1 -> DebuffIntent(
                this, listOf(),
                listOf(
                    OrphanedTileStatusInflictStrategy(TileStatus.FREEZE, 3)
                ),
                AttackIntent(this, 1, 3, Element.ICE),
                {
                    val openFreeze = api.combat.pool.chooseAmount(3, api.rng)
                    api.setTileStatus(openFreeze, TileStatus.FREEZE)
                }
            )
            2 -> VentIntent(this, 20, null)
            else -> AttackIntent(this, 30, 1, Element.ICE)
        }
        if (api.heroHasStatus(Overloaded::class) && originalIntent !is AttackIntent && originalIntent !is DebuffIntent) {
            return BuffIntent(this, listOf(Strength(1)), AttackIntent(this, 10, 1, Element.ICE))
        }
        if (justOverloaded && originalIntent is VentIntent) {
            justOverloaded = false
            return VentIntent(this, 40, null)
        }
        return originalIntent
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }

    @CombatSubscribe
    fun onOverload(ev: EnemyStatusChangeEvent, api: CombatApi) {
        if (ev.enemy == this && ev.status is Overloaded) {
            justOverloaded = true
        }
    }
}
