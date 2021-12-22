package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.DamageOrigin
import com.pipai.dragontiles.combat.DamageTarget
import com.pipai.dragontiles.data.Element

class Strength(amount: Int) : Status(amount) {
    override val strId = "base:status:Strength"
    override val assetName = "strength.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Strength(amount)
    }

    override fun queryFlatAdjustment(origin: DamageOrigin, target: DamageTarget, element: Element): Int {
        return if (origin == DamageOrigin.SELF_ATTACK && target == DamageTarget.OPPONENT) {
            amount
        } else {
            0
        }
    }
}
