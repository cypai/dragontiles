package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.EntitySubscription
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.RadialSpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.utils.*

class AttackCircleSystem : IteratingSystem(allOf()) {

    private val mRadial by require<RadialSpriteComponent>()
    private val mAttackCircle by require<AttackCircleComponent>()

    private val baseIncrement = 1f / 60f

    private val circleIndexMap: MutableMap<Int, MutableList<Int?>> = mutableMapOf()

    override fun initialize() {
        world.aspectSubscriptionManager.get(allOf(AttackCircleComponent::class))
                .addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
                    override fun inserted(entities: IntBag?) {
                    }

                    override fun removed(entities: IntBag?) {
                        entities?.forEach {
                            val cAttackCircle = mAttackCircle.get(it)
                            circleIndexMap[cAttackCircle.enemyId]!!.replaceAll { circleId ->
                                if (circleId == cAttackCircle.id) {
                                    null
                                } else {
                                    circleId
                                }
                            }
                        }
                    }
                })
    }

    fun handleNewAttackCircle(id: Int) {
        val cAttackCircle = mAttackCircle.get(id)
        addToCircleIndex(cAttackCircle.enemyId, cAttackCircle.id)
    }

    private fun addToCircleIndex(enemyId: Int, circleId: Int) {
        if (!circleIndexMap.containsKey(enemyId)) {
            circleIndexMap[enemyId] = mutableListOf()
        }
        val indexes = circleIndexMap[enemyId]!!
        val firstIndex = indexes.indexOfFirst { it == null }
        if (firstIndex == -1) {
            indexes.add(circleId)
        } else {
            indexes.removeAt(firstIndex)
            indexes.add(firstIndex, circleId)
        }
    }

    fun getCircleIndex(enemyId: Int, circleId: Int): Int {
        return circleIndexMap[enemyId]!!.indexOf(circleId)
    }

    override fun process(entityId: Int) {
        val cAttackCircle = mAttackCircle.get(entityId)
        val increment = when (cAttackCircle.turnsLeft) {
            1 -> baseIncrement * 4
            2 -> baseIncrement * 3
            3 -> baseIncrement * 2
            else -> baseIncrement
        }
        if (cAttackCircle.up) {
            cAttackCircle.t += increment
        } else {
            cAttackCircle.t -= increment
        }
        if (cAttackCircle.t >= 1f) {
            cAttackCircle.up = false
        } else if (cAttackCircle.t <= 0f) {
            cAttackCircle.up = true
        }
        val cRadial = mRadial.get(entityId)
        val color = cAttackCircle.color
        cRadial.sprite.setColor(Color(color.r, color.g, color.b, cAttackCircle.t))
    }

}
