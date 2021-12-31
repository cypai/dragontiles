package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.DebuffIntent
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.AssetConfig
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.BreakStatus

class Slime : Enemy() {

    override val id: String = "base:enemies:Slime"
    override val assetName: String = "slime"
    override val assetConfig: AssetConfig = AssetConfig(AssetType.SPINE, -0.5f, 0.5f)

    override val hpMax: Int = 13
    override val fluxMax: Int = 0

    private var attacks: Int = 0

    override suspend fun init(api: CombatApi) {
        attacks = api.runData.seed.miscRng().nextInt(2) - 1
    }

    override fun getIntent(api: CombatApi): Intent {
        return when (attacks % 4) {
            0 -> DebuffIntent(this, BreakStatus(2, true), null, listOf())
            else -> AttackIntent(this, 8, 1, false, Element.ICE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        attacks++
        return getIntent(api)
    }
}
