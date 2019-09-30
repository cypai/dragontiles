package com.pipai.dragontiles.artemis.systems.animation

import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance

class ConsumeTileAnimation(private val tile: TileInstance) : Animation() {

    private lateinit var sTile: TileIdSystem

    override fun startAnimation() {
        val entityId = sTile.getEntityId(tile.id)
        world.delete(entityId)
        endAnimation()
    }

}
