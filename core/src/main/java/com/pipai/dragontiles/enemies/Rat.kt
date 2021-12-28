package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Dodge
import com.pipai.dragontiles.status.Weak

class Rat : Enemy() {

    override val id: String = "base:enemies:Rat"
    override val assetName: String = "rat.png"

    override val hpMax: Int = 2
    override val fluxMax: Int = 20

    private var flag = false

    override suspend fun init(api: CombatApi) {
        flag = api.runData.seed.miscRng().nextBoolean()
    }

    override fun getIntent(api: CombatApi): Intent {
        return if (flag) {
            DebuffIntent(this, Weak(1, true), AttackIntent(this, 8, 1, false, Element.ICE), listOf())
        } else {
            BuffIntent(this, Dodge(1), null)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent(api)
    }
}
