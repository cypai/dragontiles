package com.pipai.dragontiles.status

class Dodge(amount: Int) : Status(amount) {
    override val id = "base:status:Dodge"
    override val assetName = "dodge.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Dodge(amount)
    }
}
