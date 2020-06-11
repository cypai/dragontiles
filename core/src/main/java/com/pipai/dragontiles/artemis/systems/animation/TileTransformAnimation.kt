package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.combat.TileTransformedEvent
import com.pipai.dragontiles.data.TileSkin

class TileTransformAnimation(private val ev: TileTransformedEvent, private val tileSkin: TileSkin) : Animation() {

    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        val entityId = sTileId.getEntityId(ev.previous.id)
        mTile.get(entityId).tile = ev.tile
        sTileId.notify(entityId)
        mSprite.get(entityId).sprite = Sprite(tileSkin.regionFor(ev.tile.tile))
        endAnimation()
    }

}
