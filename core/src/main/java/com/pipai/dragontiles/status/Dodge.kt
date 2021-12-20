package com.pipai.dragontiles.status

class Dodge(amount: Int) : Status(amount) {
    override val strId = "base:status:Dodge"
    override val assetName = "assets/binassets/graphics/status/dodge.png"
    override val displayAmount = true

    override fun deepCopy(): Status {
        return Dodge(amount)
    }
}
