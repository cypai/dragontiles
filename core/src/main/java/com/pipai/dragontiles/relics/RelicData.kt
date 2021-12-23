package com.pipai.dragontiles.relics

import kotlinx.serialization.Serializable

@Serializable
data class RelicData(val availableRelics: MutableList<Relic>)
