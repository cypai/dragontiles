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

class Gnat : Enemy() {

    override val id: String = "base:enemies:Gnat"
    override val assetName: String = "gnat.png"
    override val assetConfig: AssetConfig = AssetConfig(AssetType.SPRITE, 1f)

    override val hpMax: Int = 1
    override val fluxMax: Int = 20

    var n = 1

    override fun getIntent(api: CombatApi): Intent {
        return DebuffIntent(
            this,
            listOf(Weak(1, false), Vulnerable(1, false)),
            listOf(),
            AttackIntent(this, 1, n, Element.NONE)
        )
    }

    override fun nextIntent(api: CombatApi): Intent {
        n++
        return getIntent(api)
    }
}
