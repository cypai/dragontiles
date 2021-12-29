package com.pipai.dragontiles.status

class Dodge(amount: Int) : Status(amount) {
    override val id = "base:status:Dodge"
    override val assetName = "dodge.png"
    override val displayAmount = true
    override val isDebuff: Boolean = false

    override fun deepCopy(): Status {
        return Dodge(amount)
    }
}
