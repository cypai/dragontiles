package com.pipai.dragontiles.status

open class SimpleStatus(
    override val id: String, override val assetName: String, override val displayAmount: Boolean, amount: Int
) : Status(amount) {
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return SimpleStatus(id, assetName, displayAmount, amount)
    }
}
