package com.pipai.dragontiles.artemis.systems

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.TimerComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class TimerSystem : IteratingSystem(allOf()) {
    private val mTimer by require<TimerComponent>()

    override fun process(entityId: Int) {
        val cTimer = mTimer.get(entityId)
        cTimer.t += world.delta
        if (cTimer.t >= cTimer.maxT) {
            cTimer.onEndCallback?.invoke()
            when (cTimer.onEnd) {
                EndStrategy.REMOVE -> mTimer.remove(entityId)
                EndStrategy.RESTART -> cTimer.t = 0f
                EndStrategy.DESTROY -> world.delete(entityId)
                EndStrategy.REVERSE_THEN_REMOVE -> mTimer.remove(entityId)
            }
        }
    }
}
