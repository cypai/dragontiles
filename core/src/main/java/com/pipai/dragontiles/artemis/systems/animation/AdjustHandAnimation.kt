package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class AdjustHandAnimation(private val tileLocations: List<Pair<TileInstance, Int>>,
                          layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var sTileId: TileIdSystem
    private lateinit var sCombatUi: CombatUiSystem

    override fun startAnimation() {
        val activeTiles = sCombatUi.activeTiles()
        tileLocations.forEach { (tile, index) ->
            val entityId = sTileId.getEntityId(tile.id)
            if (tile in activeTiles) {
                moveTile(entityId, layout.handActiveTilePosition(index), 0.1f) {
                    endAnimation()
                }
            } else {
                moveTile(entityId, layout.handTilePosition(index), 0.3f) {
                    endAnimation()
                }
            }
        }
    }

}
