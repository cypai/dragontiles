package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout

class AdjustOpenPoolAnimation(private val openPool: List<TileInstance>,
                              layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        openPool.forEachIndexed { index, tile ->
            val entityId = sTileId.getEntityId(tile.id)
            val cPath = mPath.create(entityId)
            cPath.endpoints.add(mXy.get(entityId).toVector2())
            cPath.endpoints.add(layout.openTilePosition(index))
            cPath.interpolation = Interpolation.pow3Out
            cPath.maxT = 20
            cPath.onEnd = EndStrategy.REMOVE
            cPath.onEndpoint = { endAnimation() }
        }
    }

}
