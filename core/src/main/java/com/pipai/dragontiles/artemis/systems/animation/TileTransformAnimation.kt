package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin

class TileTransformAnimation(private val tile: TileInstance, private val tileSkin: TileSkin) : Animation() {

    private lateinit var mSprite: ComponentMapper<SpriteComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        val entityId = sTileId.getEntityId(tile.id)
        mSprite.get(entityId).sprite = Sprite(tileSkin.regionFor(tile.tile))
        endAnimation()
    }

}
