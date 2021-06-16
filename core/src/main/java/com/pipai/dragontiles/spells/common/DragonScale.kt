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

    companion object {
        val FIRE_DRAGON_SCALE = SimpleStatus("base:status:FireDragonScale", false, 1)
        val ICE_DRAGON_SCALE = SimpleStatus("base:status:IceDragonScale", false, 1)
        val LIGHTNING_DRAGON_SCALE = SimpleStatus("base:status:LightningDragonScale", false, 1)
    }

    override fun newClone(upgraded: Boolean): DragonScale {
        return DragonScale(upgraded)
    }

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.exhaust(this)
        var register = true
        register = register && !api.removeHeroStatus(FIRE_DRAGON_SCALE)
        register = register && !api.removeHeroStatus(ICE_DRAGON_SCALE)
        register = register && !api.removeHeroStatus(LIGHTNING_DRAGON_SCALE)
        when (elemental(components())) {
            Element.FIRE -> api.addStatusToHero(FIRE_DRAGON_SCALE)
            Element.ICE -> api.addStatusToHero(ICE_DRAGON_SCALE)
            Element.LIGHTNING -> api.addStatusToHero(LIGHTNING_DRAGON_SCALE)
            else -> {
            }
        }
        if (register) api.register(DragonScaleImpl())
    }

    class DragonScaleImpl {
        @CombatSubscribe
        suspend fun onDraw(ev: DrawEvent, api: CombatApi) {
            val suit = when {
                api.heroHasStatus(FIRE_DRAGON_SCALE) -> Suit.FIRE
                api.heroHasStatus(ICE_DRAGON_SCALE) -> Suit.ICE
                api.heroHasStatus(LIGHTNING_DRAGON_SCALE) -> Suit.LIGHTNING
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

