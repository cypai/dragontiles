package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class TileDiscardAnimation(
    private val tiles: List<TileInstance>,
    layout: CombatUiLayout
) : TileAnimation(layout) {

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        tiles.forEach { tile ->
            val entityId = sTileId.getEntityId(tile.id)
            moveTile(entityId, layout.discardPosition, 0.8f) {
                world.delete(entityId)
                endAnimation()
            }
        }
        endAnimation()
    }

}
