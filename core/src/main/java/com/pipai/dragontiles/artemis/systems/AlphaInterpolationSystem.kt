package com.pipai.dragontiles.artemis.systems

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.artemis.components.AlphaInterpolationComponent
import com.pipai.dragontiles.artemis.components.EndStrategy
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TextLabelComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.require

class AlphaInterpolationSystem : IteratingSystem(allOf()) {
    private val mAlpha by require<AlphaInterpolationComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mText by mapper<TextLabelComponent>()

    override fun process(entityId: Int) {
        val cAlpha = mAlpha.get(entityId)

        cAlpha.t += world.delta
        val alpha = cAlpha.interpolation.apply(
            cAlpha.startAlpha,
            cAlpha.targetAlpha,
            cAlpha.t / cAlpha.maxT
        )
        mSprite.getSafe(entityId, null)?.sprite?.setAlpha(alpha)
        mText.getSafe(entityId, null)?.color?.a = alpha
        if (cAlpha.t > cAlpha.maxT) {
            cAlpha.onEndpoint?.invoke(cAlpha)
            when (cAlpha.onEnd) {
                EndStrategy.REMOVE -> mAlpha.remove(entityId)
                EndStrategy.DESTROY -> world.delete(entityId)
                EndStrategy.RESTART -> cAlpha.t = 0f
                EndStrategy.REVERSE_THEN_REMOVE -> {
                    cAlpha.t = 0f
                    val tmp = cAlpha.targetAlpha
                    cAlpha.targetAlpha = cAlpha.startAlpha
                    cAlpha.startAlpha = tmp
                    cAlpha.onEnd = EndStrategy.REMOVE
                }
            }
        }
    }
}
