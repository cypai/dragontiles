package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.EntitySubscription
import com.artemis.utils.IntBag
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.forEach
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system

class EnemyIntentSystem() : NoProcessingSystem() {

    private val intents: MutableMap<Enemy, EntityId> = mutableMapOf()

    private val mXy by mapper<XYComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mIntent by mapper<IntentComponent>()
    private val mMutualDestroy by mapper<MutualDestroyComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()

    private val sCombat by system<CombatControllerSystem>()

    override fun initialize() {
        world.aspectSubscriptionManager.get(allOf(EnemyComponent::class))
            .addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
                override fun inserted(entities: IntBag?) {
                    entities?.forEach { enemyEntityId ->
                        val intentId: EntityId = world.create()
                        val cXy = mXy.create(intentId)
                        cXy.setXy(mXy.get(enemyEntityId).toVector2())
                        val cMutualDestroy = mMutualDestroy.create(intentId)
                        cMutualDestroy.ids.add(enemyEntityId)
                        val cTextLabel = mTextLabel.create(intentId)
                        cTextLabel.yOffset = mSprite.get(enemyEntityId).sprite.height + 16f
                        val cIntent = mIntent.create(intentId)
                        intents[mEnemy.get(enemyEntityId).enemy] = intentId
                    }
                }

                override fun removed(entities: IntBag?) {
                }
            })
    }

    private fun elementColor(element: Element?): Color {
        return when (element) {
            Element.FIRE -> Color.RED
            Element.ICE -> Color.SKY
            Element.LIGHTNING -> Color.YELLOW
            else -> Color.WHITE
        }
    }

    fun changeIntent(enemy: Enemy, intent: Intent?) {
        val entityId = intents[enemy]!!
        val cIntent = mIntent.get(entityId)
        cIntent.intent = intent
        val cTextLabel = mTextLabel.get(entityId)
        when (intent) {
            is AttackIntent -> {
                val attackPower = sCombat.controller.api.calculateDamageOnHero(enemy, intent.element, intent.attackPower)
                cTextLabel.color = elementColor(intent.element)
                cTextLabel.text = "Attack $attackPower"
            }
            is BuffIntent -> {
                if (intent.attackIntent == null) {
                    cTextLabel.color = Color.WHITE
                    cTextLabel.text = "Buffing"
                } else {
                    val attackPower = sCombat.controller.api.calculateDamageOnHero(enemy,
                        intent.attackIntent.element,
                        intent.attackIntent.attackPower)
                    cTextLabel.color = elementColor(intent.attackIntent.element)
                    cTextLabel.text = "Buffing, Attack $attackPower"
                }
            }
            is DebuffIntent -> {
                if (intent.attackIntent == null) {
                    cTextLabel.color = Color.WHITE
                    cTextLabel.text = "Debuffing"
                } else {
                    val attackPower = sCombat.controller.api.calculateDamageOnHero(enemy,
                        intent.attackIntent.element,
                        intent.attackIntent.attackPower)
                    cTextLabel.color = elementColor(intent.attackIntent.element)
                    cTextLabel.text = "Debuffing, Attack $attackPower"
                }
            }
            is StunnedIntent -> {
                cTextLabel.color = Color.WHITE
                cTextLabel.text = "Stunned"
            }
            null -> {
                cTextLabel.text = ""
            }
        }
    }
}
