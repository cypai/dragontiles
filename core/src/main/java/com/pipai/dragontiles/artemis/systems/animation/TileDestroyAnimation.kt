package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance

class TileDestroyAnimation(private val tiles: List<TileInstance>) : Animation() {

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        tiles.forEach { tile ->
            val entityId = sTileId.getEntityId(tile.id)
            world.delete(entityId)
        }
        endAnimation()
    }

}
