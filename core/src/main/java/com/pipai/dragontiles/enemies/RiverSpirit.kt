package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.BreakStatus
import com.pipai.dragontiles.status.Ritual
import com.pipai.dragontiles.status.VentilationStatus

class RiverSpirit : Enemy() {

    override val id: String = "base:enemies:RiverSpirit"
    override val assetName: String = "river_spirit.png"

    override val hpMax: Int = 50
    override val fluxMax: Int = 20

    private var flag = false
    private var enraged = false

    override suspend fun init(api: CombatApi) {
        flag = api.runData.seed.miscRng().nextBoolean()
        api.addStatusToEnemy(this, Ritual(1))
        api.addStatusToEnemy(this, VentilationStatus(3))
    }

    override fun getIntent(api: CombatApi): Intent {
        return if (enraged) {
            FumbleIntent(this, 1, AttackIntent(this, 1, 3, Element.ICE))
        } else {
            if (flag) {
                DebuffIntent(
                    this,
                    listOf(BreakStatus(1, true)),
                    listOf(),
                    AttackIntent(this, 1, 1, Element.ICE)
                )
            } else {
                FumbleIntent(this, 1, AttackIntent(this, 1, 1, Element.ICE))
            }
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        flag = !flag
        return getIntent(api)
    }
}
