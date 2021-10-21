package com.pipai.dragontiles.artemis.systems

import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.AnchorComponent
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.PathInterpolationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.mapper

class AnchorSystem : NoProcessingSystem() {
    private val mXy by mapper<XYComponent>()
    private val mAnchor by mapper<AnchorComponent>()
    private val mPath by mapper<PathInterpolationComponent>()

    fun returnToAnchor(id: EntityId) {
        val cPath = mPath.create(id)
        cPath.setPath(mXy.get(id).toVector2(), mAnchor.get(id).toVector2(), 0.25f, Interpolation.exp10Out, EndStrategy.REMOVE)
    }
}
