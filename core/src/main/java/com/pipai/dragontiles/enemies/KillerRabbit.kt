package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.MenacingAura

class KillerRabbit : Enemy() {

    override val strId: String = "base:enemies:KillerRabbit"
    override val assetName: String = "rabbit.png"

    override val hpMax: Int = 20
    override val fluxMax: Int = 20

    private var intents = 0

    override fun getIntent(): Intent {
        return when (intents) {
            0 -> BuffIntent(this, MenacingAura(1), null)
            else -> AttackIntent(this, 13, 1, false, Element.NONE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        intents++
        return getIntent()
    }
}
