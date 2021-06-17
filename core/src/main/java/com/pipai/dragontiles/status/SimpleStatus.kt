package com.pipai.dragontiles.status

abstract class SimpleStatus(override val strId: String, override val displayAmount: Boolean, amount: Int) : Status(amount) {
}
