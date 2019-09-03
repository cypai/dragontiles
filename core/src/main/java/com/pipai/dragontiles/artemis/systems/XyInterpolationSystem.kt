package com.pipai.dragontiles.artemis.systems

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class XyInterpolationSystem : IteratingSystem(allOf()) {
    private val mPath by require<PathInterpolationComponent>()
    private val mXy by require<XYComponent>()

    override fun process(entityId: Int) {
        val cXy = mXy.get(entityId)
        val cPath = mPath.get(entityId)
        cXy.setXy(cPath.getCurrentPos())
    }
}
