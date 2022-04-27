package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.FumbleIntent
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.status.Immortality

class MoonRabbit : Enemy() {

    override val id: String = "base:enemies:MoonRabbit"
    override val assetName: String = "moon_rabbit.png"

    override val hpMax: Int = 24
    override val fluxMax: Int = 48

    private var waitTurns = 2
    private var nextWaitTurns = 3

    override fun getIntent(api: CombatApi): Intent {
        return if (waitTurns > 0) {
            FumbleIntent(this, 1, null)
        } else {
            BuffIntent(this, listOf(), null, {
                api.addAoeStatus(Immortality(1))
            })
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        waitTurns--
        if (waitTurns < 0) {
            waitTurns = nextWaitTurns
            nextWaitTurns += 2
        }
        return getIntent(api)
    }
}
