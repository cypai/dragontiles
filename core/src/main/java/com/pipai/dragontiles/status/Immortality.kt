package com.pipai.dragontiles.status

class Immortality(amount: Int) : Status(amount) {
    override val id = "base:status:Immortality"
    override val assetName = "immortality.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Immortality(amount)
    }
}
