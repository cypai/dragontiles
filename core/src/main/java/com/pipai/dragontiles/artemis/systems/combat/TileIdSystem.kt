package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.EntitySubscription
import com.artemis.utils.IntBag
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.forEach
import com.pipai.dragontiles.utils.mapper

class TileIdSystem : NoProcessingSystem() {

    private val mTile by mapper<TileComponent>()

    private val tileIdIndex: MutableMap<Int, Int> = mutableMapOf()

    override fun initialize() {
        world.aspectSubscriptionManager.get(allOf(TileComponent::class))
                .addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
                    override fun inserted(entities: IntBag?) {
                        entities?.forEach {
                            tileIdIndex[mTile.get(it).tile.id] = it
                        }
                    }

                    override fun removed(entities: IntBag?) {
                        entities?.forEach {
                            tileIdIndex.remove(mTile.get(it).tile.id)
                        }
                    }
                })
    }

    fun getEntityId(tileId: Int) = tileIdIndex[tileId]!!

    fun notify(entityId: Int) {
        tileIdIndex[mTile.get(entityId).tile.id] = entityId
    }

}
