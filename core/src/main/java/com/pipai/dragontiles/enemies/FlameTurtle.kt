package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Strength

class FlameTurtle : Enemy() {

    override val strId: String = "base:enemies:FlameTurtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 10
    override val fluxMax: Int = 30

    private var intents = 0

    override fun getIntent(): Intent {
        return when (intents) {
            0 -> AttackIntent(this, 12, 1, false, Element.FIRE)
            1 -> AttackIntent(this, 12, 1, false, Element.FIRE)
            else -> BuffIntent(this, Strength(4), AttackIntent(this, 4, 1, false, Element.FIRE))
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        intents++
        if (intents > 2) intents = 0
        return getIntent()
    }
}
