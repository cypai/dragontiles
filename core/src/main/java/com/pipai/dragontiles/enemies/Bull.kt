package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Strength

class Bull : Enemy() {

    override val strId: String = "base:enemies:Bull"
    override val assetName: String = "bull.png"

    override val hpMax: Int = 65
    override val fluxMax: Int = 35

    private var hasOverloaded = false

    override fun getIntent(): Intent {
        return if (hasOverloaded) {
            AttackIntent(this, 10, 1, false, Element.NONE)
        } else {
            BuffIntent(this, Strength(4), null)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent()
    }

    @CombatSubscribe
    fun onOverload(ev: EnemyStatusChangeEvent, api: CombatApi) {
        if (ev.enemy == this && ev.status is Overloaded) {
            hasOverloaded = true
        }
    }
}
