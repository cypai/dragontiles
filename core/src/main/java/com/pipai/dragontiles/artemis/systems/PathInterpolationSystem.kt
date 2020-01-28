package com.pipai.dragontiles.artemis.systems

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class PathInterpolationSystem : IteratingSystem(allOf()) {
    private val mPath by require<PathInterpolationComponent>()

    override fun process(entityId: Int) {
        val cPath = mPath.get(entityId)

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
}
