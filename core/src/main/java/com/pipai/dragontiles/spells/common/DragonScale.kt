package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus

class DragonScale(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:DragonScale"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = 1

    override fun newClone(upgraded: Boolean): DragonScale {
        return DragonScale(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.exhaust(this)
        var register = true
        register = register && !api.removeHeroStatus(FireDragonScale::class)
        register = register && !api.removeHeroStatus(IceDragonScale::class)
        register = register && !api.removeHeroStatus(LightningDragonScale::class)
        when (elemental(components())) {
            Element.FIRE -> api.addStatusToHero(FireDragonScale())
            Element.ICE -> api.addStatusToHero(IceDragonScale())
            Element.LIGHTNING -> api.addStatusToHero(LightningDragonScale())
            else -> {
            }
        }
        if (register) api.register(DragonScaleImpl())
    }

    class FireDragonScale : SimpleStatus("base:status:FireDragonScale", false, 1)
    class IceDragonScale : SimpleStatus("base:status:IceDragonScale", false, 1)
    class LightningDragonScale : SimpleStatus("base:status:LightningDragonScale", false, 1)

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

