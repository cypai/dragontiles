package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class FlameTurtle : Enemy() {

    override val strId: String = "base:enemies:FlameTurtle"
    override val assetName: String = "flame_turtle.png"

    override val hpMax: Int = 10
    override val fluxMax: Int = 30

    private var intents = 0

    override fun getIntent(): Intent {
        return when (intents) {
            0 -> AttackIntent(id, 12, 1, false, Element.FIRE)
            1 -> AttackIntent(id, 12, 1, false, Element.FIRE)
            else -> BuffIntent(id, Status.STRENGTH, 4, AttackIntent(id, 4, 1, false, Element.FIRE))
        }
    }

    override fun nextIntent(api: CombatApi) {
        intents++
        if (intents > 2) intents = 0
    }
}
