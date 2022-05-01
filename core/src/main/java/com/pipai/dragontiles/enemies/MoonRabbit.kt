package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Immortality

class MoonRabbit : Enemy() {

    override val id: String = "base:enemies:MoonRabbit"
    override val assetName: String = "moon_rabbit.png"

    override val hpMax: Int = 24
    override val fluxMax: Int = 48

    private var waitTurns = 4
    private var nextWaitTurns = 6

    override fun getIntent(api: CombatApi): Intent {
        return if (waitTurns > 0) {
            FumbleIntent(this, 1, null)
        } else {
            if (api.getLiveEnemies().all { it is MoonRabbit }) {
                AttackIntent(this, 10, 1, Element.NONE)
            } else {
                BuffIntent(this, listOf(), null, {
                    api.getLiveEnemies()
                        .filter { it !is MoonRabbit }
                        .forEach {
                            api.addStatusToEnemy(it, Immortality(1))
                        }
                })
            }
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
