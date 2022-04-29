package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.DebuffIntent
import com.pipai.dragontiles.combat.Intent
import com.pipai.dragontiles.data.AssetConfig
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Vulnerable
import com.pipai.dragontiles.status.Weak

class Monkey : Enemy() {

    override val id: String = "base:enemies:Monkey"
    override val assetName: String = "monkey.png"

    override val hpMax: Int = 20
    override val fluxMax: Int = 10

    override fun getIntent(api: CombatApi): Intent {
        return AttackIntent(this, 12, 1, Element.NONE)
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent(api)
    }
}
