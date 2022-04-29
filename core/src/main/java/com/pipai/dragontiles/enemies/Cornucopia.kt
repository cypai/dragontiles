package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.DoNothingIntent
import com.pipai.dragontiles.combat.DoNothingType
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.status.Minion
import com.pipai.dragontiles.status.Regen

class Cornucopia : Enemy() {

    override val id: String = "base:enemies:Cornucopia"
    override val assetName: String = "cornucopia.png"

    override val hpMax: Int = 40
    override val fluxMax: Int = 0

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Regen(10, false))
        api.addStatusToEnemy(this, Minion())
    }

    override fun getIntent(api: CombatApi): Intent {
        return DoNothingIntent(this, DoNothingType.WAITING)
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent(api)
    }
}
