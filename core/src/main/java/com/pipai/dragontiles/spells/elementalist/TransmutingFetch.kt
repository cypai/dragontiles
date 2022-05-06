package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.AddToOpenPoolEvent
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class TransmutingFetch : StandardSpell() {
    override val id: String = "base:spells:TransmutingFetch"
    override val requirement: ComponentRequirement = Single(SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
        FetchAspect(2, false),
    )

    private var element: Element = Element.FIRE
    private var justCasted = false

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        justCasted = true
        element = elemental(components())
    }

    @CombatSubscribe
    suspend fun onFetch(ev: AddToOpenPoolEvent, api: CombatApi) {
        if (justCasted) {
            justCasted = false
            ev.tiles.map { it.first }
                .filter { it.tile is Tile.ElementalTile }
                .forEach { tileInstance ->
                    val tile = tileInstance.tile as Tile.ElementalTile
                    when (element) {
                        Element.FIRE -> api.transformTile(
                            tileInstance,
                            Tile.ElementalTile(Suit.FIRE, tile.number),
                            true
                        )
                        Element.ICE -> api.transformTile(tileInstance, Tile.ElementalTile(Suit.ICE, tile.number), true)
                        Element.LIGHTNING -> api.transformTile(
                            tileInstance,
                            Tile.ElementalTile(Suit.LIGHTNING, tile.number),
                            true
                        )
                        else -> {}
                    }
                }
        }
    }
}
