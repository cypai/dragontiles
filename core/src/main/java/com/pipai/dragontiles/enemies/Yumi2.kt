package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.MenacingAura

class Yumi2 : Enemy() {

    override val id: String = "base:enemies:Yumi"
    override val assetName: String = "rabbit_youkai.png"

    override val hpMax: Int = 40
    override val fluxMax: Int = 80

    override fun getIntent(api: CombatApi): Intent {
        return BuffIntent(this, listOf(MenacingAura(1)), AttackIntent(this, 20, 1, Element.NONE))
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent(api)
    }
}
