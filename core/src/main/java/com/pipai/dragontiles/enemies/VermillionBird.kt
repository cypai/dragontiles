package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Strength

class VermillionBird : Enemy() {

    override val id: String = "base:enemies:VermillionBird"
    override val assetName: String = "vermillion_bird.png"

    override val hpMax: Int = 12
    override val fluxMax: Int = 30

    private var turns: Int = 0

    override suspend fun init(api: CombatApi) {
        turns = api.runData.seed.miscRng().nextInt(0, 3)
    }

    override fun getIntent(api: CombatApi): Intent {
        return when (turns % 3) {
            0 -> AttackIntent(this, 1, 7, Element.NONE)
            1 -> AttackIntent(this, 12, 1, Element.NONE)
            else -> BuffIntent(this, listOf(Strength(1)), null)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
