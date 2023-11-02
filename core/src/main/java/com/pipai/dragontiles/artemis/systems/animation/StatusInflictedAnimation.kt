package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.statusAssetPath

class StatusInflictedAnimation(private val status: Status) : Animation() {
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mAlpha: ComponentMapper<AlphaInterpolationComponent>

    override fun startAnimation() {
        val targetEntityId = when (val combatant = status.combatant!!) {
            is Combatant.HeroCombatant -> {
                world.fetch(allOf(HeroComponent::class)).first()
            }
            is Combatant.EnemyCombatant -> {
                world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == combatant.enemy }
            }
        }
        val cTargetXy = mXy.get(targetEntityId)
        val entityId = world.create()
        val cXy = mXy.create(entityId)
        cXy.setXy(cTargetXy.x, cTargetXy.y)
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(game.assets.get(statusAssetPath(status.assetName), Texture::class.java))
        val alpha = 0.5f
        cSprite.sprite.setAlpha(alpha)
        val cAlpha = mAlpha.create(entityId)
        cAlpha.set(alpha, 0f, 0.5f, Interpolation.linear, EndStrategy.DESTROY)
        cAlpha.onEndpoint = { endAnimation() }
    }

}
