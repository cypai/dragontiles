package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HandLocationComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class DrawTileAnimation(private val tile: TileInstance,
                        private val handLocation: Int,
                        layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var mHand: ComponentMapper<HandLocationComponent>

    override fun startAnimation() {
        val entityId = createTile(tile, layout.drawPosition.x, layout.drawPosition.y)
        val cHand = mHand.create(entityId)
        cHand.setByLocation(handLocation)
        moveTile(entityId, layout.handTilePosition(handLocation)) {
            endAnimation()
        }
    }

}
