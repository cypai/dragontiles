package com.pipai.dragontiles.status

class Minion : Status(1) {
    override val id = "base:status:Minion"
    override val assetName = "minion.png"
    override val displayAmount = false
    override val negativeAllowed = false
    override fun isDebuff(): Boolean = false

    override fun deepCopy(): Status {
        return Minion()
    }
}
