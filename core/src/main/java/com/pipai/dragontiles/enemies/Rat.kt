package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Dodge
import com.pipai.dragontiles.status.Weak

class Rat : Enemy() {

    override val strId: String = "base:enemies:Rat"
    override val assetName: String = "rat.png"

    override val hpMax: Int = 16
    override val fluxMax: Int = 0

    private var flag = false

    override suspend fun init(api: CombatApi) {
        flag = api.runData.rng.nextBoolean()
    }

    override fun getIntent(): Intent {
        return if (flag) {
            DebuffIntent(this, Weak(1, true), AttackIntent(this, 8, 1, false, Element.ICE))
        } else {
            BuffIntent(this, Dodge(1), null)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent()
    }
}
