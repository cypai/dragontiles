package com.pipai.dragontiles.artemis.systems

import com.artemis.EntitySubscription
import com.artemis.utils.IntBag
import com.pipai.dragontiles.artemis.components.AnchoredLineComponent
import com.pipai.dragontiles.artemis.components.MutualDestroyComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.forEach
import com.pipai.dragontiles.utils.mapper

class MutualDestroySystem : NoProcessingSystem() {
    // TODO: Maybe use registration instead to avoid stale references?

    private val mMutualDestroy by mapper<MutualDestroyComponent>()

    override fun initialize() {
        world.aspectSubscriptionManager.get(allOf(MutualDestroyComponent::class))
                .addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
                    override fun inserted(entities: IntBag?) {
                    }

                    override fun removed(entities: IntBag?) {
                        entities?.forEach {
                            val cMutualDestroy = mMutualDestroy.get(it)
                            cMutualDestroy.ids.forEach { id -> world.delete(id) }
                        }
                    }
                })
    }

}
