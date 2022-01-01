package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.esotericsoftware.spine.AnimationState
import com.esotericsoftware.spine.Event
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.SpineComponent
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class SpineAnimation(val enemy: Enemy, val animation: String, val endEvent: String?) : Animation() {
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mSpine: ComponentMapper<SpineComponent>

    override fun startAnimation() {
        val entityId = world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == enemy }
        val cSpine = mSpine.get(entityId)
        val trackEntry = cSpine.state.addAnimation(0, animation, false, 0f)
        cSpine.state.addAnimation(0, "Idle", true, 0f)
        trackEntry.listener = object : AnimationState.AnimationStateListener {
            override fun start(entry: AnimationState.TrackEntry?) {
            }

            override fun interrupt(entry: AnimationState.TrackEntry?) {
            }

            override fun end(entry: AnimationState.TrackEntry?) {
                endAnimation()
            }

            override fun complete(entry: AnimationState.TrackEntry?) {
            }

            override fun event(entry: AnimationState.TrackEntry?, event: Event?) {
                if (event!!.data.name == endEvent) {
                    endAnimation()
                }
            }

            override fun dispose(entry: AnimationState.TrackEntry?) {
            }
        }
    }
}
