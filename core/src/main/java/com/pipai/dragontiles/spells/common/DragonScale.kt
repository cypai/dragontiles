package com.pipai.dragontiles.spells.common

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.DrawEvent
import com.pipai.dragontiles.combat.Status
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class DragonScale(upgraded: Boolean) : StandardSpell(upgraded) {
    override val id: String = "base:spells:DragonScale"
    override val requirement: ComponentRequirement = Sequential(9, SuitGroup.ELEMENTAL)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.COMMON

    override var repeatableMax: Int = 1

    companion object {
        val FIRE_DRAGON_SCALE = Status("base:status:FireDragonScale", false)
        val ICE_DRAGON_SCALE = Status("base:status:IceDragonScale", false)
        val LIGHTNING_DRAGON_SCALE = Status("base:status:LightningDragonScale", false)
    }

    override fun newClone(upgraded: Boolean): DragonScale {
        return DragonScale(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.exhaust(this)
        var register = true
        register = register && api.removeStatus(FIRE_DRAGON_SCALE) == null
        register = register && api.removeStatus(ICE_DRAGON_SCALE) == null
        register = register && api.removeStatus(LIGHTNING_DRAGON_SCALE) == null
        when (elemental(components())) {
            Element.FIRE -> api.changeStatusIncrement(FIRE_DRAGON_SCALE, 1)
            Element.ICE -> api.changeStatusIncrement(ICE_DRAGON_SCALE, 1)
            Element.LIGHTNING -> api.changeStatusIncrement(LIGHTNING_DRAGON_SCALE, 1)
            else -> {
            }
        }
        if (register) api.register(DragonScaleImpl())
    }

    class DragonScaleImpl {
        @CombatSubscribe
        suspend fun onDraw(ev: DrawEvent, api: CombatApi) {
            val suit = when {
                api.hasStatus(FIRE_DRAGON_SCALE) -> Suit.FIRE
                api.hasStatus(ICE_DRAGON_SCALE) -> Suit.ICE
                api.hasStatus(LIGHTNING_DRAGON_SCALE) -> Suit.LIGHTNING
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

