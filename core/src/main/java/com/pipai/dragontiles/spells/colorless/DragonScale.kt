package com.pipai.dragontiles.spells.colorless

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus

class DragonScale : StandardSpell() {
    override val id: String = "base:spells:DragonScale"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.POWER
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.SPECIAL
    override val aspects: MutableList<SpellAspect> = mutableListOf()
    override val scoreable: Boolean = true

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        when (elemental(components())) {
            Element.FIRE -> api.addStatusToHero(FireDragonScale())
            Element.ICE -> api.addStatusToHero(IceDragonScale())
            Element.LIGHTNING -> api.addStatusToHero(LightningDragonScale())
            else -> {
            }
        }
    }

    class FireDragonScale :
        SimpleStatus("base:status:FireDragonScale", "red.png", false, 1)

    class IceDragonScale :
        SimpleStatus("base:status:IceDragonScale", "blue.png", false, 1)

    class LightningDragonScale :
        SimpleStatus("base:status:LightningDragonScale", "yellow.png", false, 1)

    class DragonScaleImpl {
        @CombatSubscribe
        suspend fun onDraw(ev: DrawEvent, api: CombatApi) {
            val suit = when {
                api.heroHasStatus(FireDragonScale::class) -> Suit.FIRE
                api.heroHasStatus(IceDragonScale::class) -> Suit.ICE
                api.heroHasStatus(LightningDragonScale::class) -> Suit.LIGHTNING
                else -> return
            }
            ev.tiles.map { it.first }
                .filter { it.tile is Tile.ElementalTile }
                .forEach {
                    val tile = it.tile as Tile.ElementalTile
                    api.transformTile(it, Tile.ElementalTile(suit, tile.number), false)
                }
        }
    }
}

