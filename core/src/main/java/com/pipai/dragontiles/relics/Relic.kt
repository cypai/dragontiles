package com.pipai.dragontiles.relics

import com.pipai.dragontiles.combat.CombatApi

abstract class Relic {
    abstract val id: String

    lateinit var api: CombatApi
}
