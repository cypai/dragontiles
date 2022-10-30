package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class AdjustPoolAnimation(private val pool: List<TileInstance>,
                          layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        if (pool.isNotEmpty()) {
            pool.forEachIndexed { index, tile ->
                val entityId = sTileId.getEntityIdSafe(tile.id)
                if (entityId != null) {
                    moveTile(entityId, layout.openTilePosition(index)) {
                        endAnimation()
                    }
                }
            }
        } else {
            endAnimation()
        }
    }

}
