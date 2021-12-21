package com.pipai.dragontiles.relics

data class RelicData(val availableRelics: MutableList<Relic>) {
    companion object {
        val ALL_RELICS: List<Relic> = listOf(
            Bamboo(),
            Cherry(),
            Coffee(),
            Elixir(),
            Ginsengfruit(),
            Peach(),
            Tea(),
        )
    }
}
