package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.MenacingAura
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Strength

class Yumi : Enemy() {

    override val strId: String = "base:enemies:Yumi"
    override val assetName: String = "rabbit_youkai.png"

    override val hpMax: Int = 40
    override val fluxMax: Int = 50

    private var turns: Int = 1
    private var hasOverloaded = false

    override fun getIntent(): Intent {
        return if (hasOverloaded || turns > 3) {
            BuffIntent(this, MenacingAura(1), AttackIntent(this, 20, 1, false, Element.NONE))
        } else {
            FumbleIntent(this, 1, null)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent()
    }

    @CombatSubscribe
    fun onTurnEnd(ev: EnemyTurnEndEvent, api: CombatApi) {
        turns++
    }

    @CombatSubscribe
    fun onOverload(ev: EnemyStatusChangeEvent, api: CombatApi) {
        if (ev.enemy == this && ev.status is Overloaded) {
            hasOverloaded = true
        }
    }
}
