package com.pipai.dragontiles.artemis.systems

import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class PathInterpolationSystem : IteratingSystem(allOf()) {
    private val mPath by require<PathInterpolationComponent>()
    private val mXy by require<XYComponent>()

    override fun process(entityId: Int) {
        val cPath = mPath.get(entityId)
        val cXy = mXy.get(entityId)
        cXy.setXy(cPath.getCurrentPos())

        cPath.t += world.delta
        if (cPath.t > cPath.maxT) {
            cPath.endpointIndex++
            cPath.t = 0f
            cPath.onEndpoint?.invoke(cPath)
            if (cPath.endpointIndex >= cPath.endpoints.size - 1) {
                when (cPath.onEnd) {
                    EndStrategy.REMOVE -> mPath.remove(entityId)
                    EndStrategy.DESTROY -> world.delete(entityId)
                    EndStrategy.RESTART -> cPath.endpointIndex = 0
                }
            }
        }
    }

    fun moveToLocation(id: EntityId, location: Vector2) {
        val cXy = mXy.get(id)
        val cPath = mPath.create(id)
        cPath.setPath(cXy.toVector2(), location, 0.25f, Interpolation.exp10Out, EndStrategy.REMOVE)
    }

    fun moveToLocation(id: EntityId, x: Float, y: Float) {
        val cXy = mXy.get(id)
        val cPath = mPath.create(id)
        cPath.setPath(cXy.toVector2(), Vector2(x, y), 0.25f, Interpolation.exp10Out, EndStrategy.REMOVE)
    }
}
