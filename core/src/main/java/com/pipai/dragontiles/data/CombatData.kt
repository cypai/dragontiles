package com.pipai.dragontiles.data

data class CountdownAttack(val id: Int,
                           var baseDamage: Int,
                           var multiplier: Int,
                           var element: Element,
                           var turnsLeft: Int,
                           var name: String,
                           var description: String)
