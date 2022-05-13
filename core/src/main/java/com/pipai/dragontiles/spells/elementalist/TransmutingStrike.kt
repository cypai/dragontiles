package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class TransmutingStrike : StandardSpell() {
    override val id: String = "base:spells:TransmutingStrike"
    override val requirement: ComponentRequirement = Sequential(3)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.COMMON
    override val glowType: GlowType = GlowType.ELEMENTED
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(9),
        FluxGainAspect(6),
        DrawAspect(2, false),
    )

    private var element: Element = Element.FIRE
    private var justCasted = false

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        element = elemental(components())
        justCasted = true
        val target = api.getEnemy(params.targets.first())
        api.attack(target, element, baseDamage(), flags())
    }

    @CombatSubscribe
    suspend fun onDraw(ev: DrawEvent, api: CombatApi) {
        if (justCasted) {
            justCasted = false
            val tileInstance = ev.tiles.first().first
            val tile = tileInstance.tile
            if (tile is Tile.ElementalTile) {
                when (element) {
                    Element.FIRE -> api.transformTile(tileInstance, Tile.ElementalTile(Suit.FIRE, tile.number), true)
                    Element.ICE -> api.transformTile(tileInstance, Tile.ElementalTile(Suit.ICE, tile.number), true)
                    Element.LIGHTNING -> api.transformTile(
                        tileInstance,
                        Tile.ElementalTile(Suit.LIGHTNING, tile.number),
                        true
                    )
                    else -> {}
                }
            } else {
                val number = api.rng.nextInt(1, 10)
                when (element) {
                    Element.FIRE -> api.transformTile(tileInstance, Tile.ElementalTile(Suit.FIRE, number), true)
                    Element.ICE -> api.transformTile(tileInstance, Tile.ElementalTile(Suit.ICE, number), true)
                    Element.LIGHTNING -> api.transformTile(
                        tileInstance,
                        Tile.ElementalTile(Suit.LIGHTNING, number),
                        true
                    )
                    else -> {}
                }
            }
        }
    }
}
