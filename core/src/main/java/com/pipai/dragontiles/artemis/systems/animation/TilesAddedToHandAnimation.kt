package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.HandLocationComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class TilesAddedToHandAnimation(private val tiles: List<Pair<TileInstance, Int>>,
                                layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var mHand: ComponentMapper<HandLocationComponent>

    override fun startAnimation() {
        tiles.forEach { (tile, handIndex) ->
            val position = layout.handTilePosition(handIndex)
            val entityId = createTile(tile, position.x, position.y)
            val cHand = mHand.create(entityId)
            cHand.setByLocation(handIndex)
        }
        endAnimation()
    }

}
