package com.pipai.dragontiles.status

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.combat.DamageAdjustable
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.utils.DeepCopyable

abstract class Status(var amount: Int) : Localized, DamageAdjustable, DeepCopyable<Status> {

    abstract val assetName: String
    abstract val displayAmount: Boolean
    open val negativeAllowed: Boolean = false
    abstract fun isDebuff(): Boolean

    var combatant: Combatant? = null

    open suspend fun onInflict(api: CombatApi) {
    }

    override fun queryFlatAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Int = 0

    override fun queryScaledAdjustment(
        origin: Combatant?,
        target: Combatant?,
        element: Element,
        flags: List<CombatFlag>
    ): Float = 1f
}

class GenericStatus(amount: Int, override val id: String = "base:status:Generic") : Status(amount) {
    override val assetName: String = ""
    override val displayAmount: Boolean = false
    override fun isDebuff(): Boolean = false
    override fun deepCopy(): Status {
        return GenericStatus(amount)
    }
}
