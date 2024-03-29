package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class DrawToOpenPoolAnimation(
    private val tile: TileInstance,
    private val poolLocation: Int,
    layout: CombatUiLayout
) : TileAnimation(layout) {

    override fun startAnimation() {
        val entityId = createTile(tile, layout.drawPosition.x, layout.drawPosition.y)
        moveTile(entityId, layout.openTilePosition(poolLocation)) {
            endAnimation()
        }
    }

}
