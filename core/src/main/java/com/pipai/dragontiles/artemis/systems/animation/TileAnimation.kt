package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.TileClickEvent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

abstract class TileAnimation(protected val layout: CombatUiLayout) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>
    private lateinit var mDepth: ComponentMapper<DepthComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>

    fun createTile(tile: TileInstance,
                   x: Float,
                   y: Float): Int {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(layout.tileSkin.regionFor(tile.tile))
        mDepth.create(entityId)
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cClickable = mClickable.create(entityId)
        cClickable.eventGenerator = { TileClickEvent(entityId, it) }
        return entityId
    }

    fun moveTile(entityId: Int, location: Vector2, t: Float, endCallback: (PathInterpolationComponent) -> Unit) {
        val cXy = mXy.get(entityId)
        val cPath = mPath.create(entityId)
        cPath.setPath(cXy.toVector2(), location, t, Interpolation.pow3Out, EndStrategy.REMOVE)
        cPath.onEndpoint = endCallback
    }

    fun moveTile(entityId: Int, location: Vector2, endCallback: (PathInterpolationComponent) -> Unit) {
        moveTile(entityId, location, 0.4f, endCallback)
    }
}
