package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Overloaded

class Minotaur : Enemy() {

    override val id: String = "base:enemies:Minotaur"
    override val assetName: String = "minotaur.png"

    override val hpMax: Int = 80
    override val fluxMax: Int = 0

    private var intents = 0

    override fun getIntent(api: CombatApi): Intent {
        if (api.heroHasStatus(Overloaded::class)) {
            AttackIntent(this, 20, 1, false, Element.NONE)
        }
        return when (intents) {
            0 -> DebuffIntent(this, BreakStatus(2, true), null, listOf())
            1 -> AttackIntent(this, 20, 1, false, Element.NONE)
            else -> AttackIntent(this, 20, 1, false, Element.NONE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        if (!api.heroHasStatus(Overloaded::class)) {
            intents++
            if (intents > 2) intents = 0
        }
        return getIntent(api)
    }
}
