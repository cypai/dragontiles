package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Strength

class LargeTurtle : Enemy() {

    override val id: String = "base:enemies:LargeTurtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 10
    override val fluxMax: Int = 30

    private var intents = 0

    override fun getIntent(api: CombatApi): Intent {
        return when (intents) {
            0 -> AttackIntent(this, 12, 1, Element.NONE)
            1 -> AttackIntent(this, 12, 1, Element.NONE)
            else -> BuffIntent(this, Strength(4), AttackIntent(this, 4, 1, Element.NONE))
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        intents++
        if (intents > 2) intents = 0
        return getIntent(api)
    }
}
