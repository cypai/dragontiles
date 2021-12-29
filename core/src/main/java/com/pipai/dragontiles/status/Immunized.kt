package com.pipai.dragontiles.status

class Immunized(amount: Int) : Status(amount) {
    override val id = "base:status:Immunized"
    override val assetName = "immunized.png"
    override val displayAmount = true
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Immunized(amount)
    }
}
