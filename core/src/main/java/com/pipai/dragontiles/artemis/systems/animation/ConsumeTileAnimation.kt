package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.World
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance

class ConsumeTileAnimation(world: World, private val tile: TileInstance) : Animation(world) {

    private lateinit var sTile: TileIdSystem

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        val entityId = sTile.getEntityId(tile.id)
        world.delete(entityId)
        endAnimation()
    }

}
