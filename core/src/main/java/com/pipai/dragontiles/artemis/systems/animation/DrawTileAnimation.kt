package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.data.Tile

class DrawTileAnimation(private val world: World, private val tile: Tile, private val handLocation: Int) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mHand: ComponentMapper<HandLocationComponent>

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cXy = mXy.create(entityId)
        cXy.setXy(0f, 0f)
        val cHand = mHand.create(entityId)
        cHand.setByLocation(handLocation)
        val cPath = mPath.create(entityId)
        cPath.endpoints.add(cXy.toVector2())
        cPath.endpoints.add(Vector2(cHand.x, cHand.y))
        cPath.interpolation = Interpolation.pow3Out
        cPath.maxT = 30
        cPath.onEnd = EndStrategy.REMOVE
        cPath.onEndpoint = { observer.notify(this) }
    }

}
