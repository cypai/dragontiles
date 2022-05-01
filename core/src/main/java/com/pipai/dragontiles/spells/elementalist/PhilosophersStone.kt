package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.LifeType
import com.pipai.dragontiles.data.StarType
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.spells.*

class PhilosophersStone : StandardSpell() {
    override val id: String = "base:spells:PhilosophersStone"
    override val requirement: ComponentRequirement = ForbidTransformFreeze(
        this,
        Single(SuitGroup.ANY_NO_FUMBLE)
    )
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.NONE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        TransformAspect(),
        FluxGainAspect(7),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
    }

    override suspend fun handleComponents(api: CombatApi) {
        val tileOptions: MutableList<Tile> = mutableListOf(
            Tile.LifeTile(LifeType.LIFE),
            Tile.LifeTile(LifeType.MIND),
            Tile.LifeTile(LifeType.SOUL),
            Tile.StarTile(StarType.EARTH),
            Tile.StarTile(StarType.MOON),
            Tile.StarTile(StarType.SUN),
            Tile.StarTile(StarType.STAR),
        )
        val target = api.queryTileOptions("Choose a transformation target", null, tileOptions, 1, 1)
        val tile = components().first()
        api.transformTile(tile, target.first(), true)
    }
}
