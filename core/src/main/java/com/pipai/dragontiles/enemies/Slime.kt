package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element

class Slime : Enemy() {

    override val strId: String = "base:enemies:Slime"
    override val assetName: String = "slime.png"

    override val hpMax: Int = 13
    override val fluxMax: Int = 0

    private var attacks: Int = 0

    override fun init(api: CombatApi) {
        attacks = api.runData.rng.nextInt(2) - 1
    }

    override fun getIntent(): Intent {
        return when (attacks % 4) {
            0 -> DebuffIntent(id, Status.ICE_BREAK, 2, null)
            else -> AttackIntent(id, 8, 1, false, Element.ICE)
        }
    }

    override fun nextIntent(api: CombatApi) {
        attacks++
    }
}
