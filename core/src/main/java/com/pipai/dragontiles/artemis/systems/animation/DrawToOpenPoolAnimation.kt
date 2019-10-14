package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin

class DrawToOpenPoolAnimation(private val tile: TileInstance, private val poolLocation: Int, private val tileSkin: TileSkin) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>

    override fun startAnimation() {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(tileSkin.regionFor(tile.tile))
        val cXy = mXy.create(entityId)
        cXy.setXy(0f, 0f)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(Vector2(64f + 32f * poolLocation, 128f))
        cPath.interpolation = Interpolation.pow3Out
        cPath.maxT = 30
        cPath.onEnd = EndStrategy.REMOVE
        cPath.onEndpoint = { endAnimation() }
    }

}
