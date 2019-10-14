package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.artemis.components.ClickableComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.TileClickEvent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin

abstract class TileCreationAnimation(private val tileSkin: TileSkin) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>

    fun createTile(tile: TileInstance,
                   x: Float,
                   y: Float): Int {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(tileSkin.regionFor(tile.tile))
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cClickable = mClickable.create(entityId)
        cClickable.event = TileClickEvent(entityId)
        return entityId
    }

}
