package com.pipai.dragontiles.status

import kotlin.reflect.full.createInstance

abstract class SimpleStatus(
    override val id: String, override val assetName: String, override val displayAmount: Boolean, amount: Int
) : Status(amount) {

    override fun deepCopy(): Status {
        return this::class.createInstance()
    }
}
