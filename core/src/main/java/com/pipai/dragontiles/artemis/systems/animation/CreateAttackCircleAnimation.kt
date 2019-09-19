package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.artemis.World
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.combat.AttackCircleSystem
import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.misc.RadialSprite
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class CreateAttackCircleAnimation(world: World,
                                  private val game: DragonTilesGame,
                                  private val enemy: Enemy,
                                  private val attack: CountdownAttack) : Animation(world) {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mRadial: ComponentMapper<RadialSpriteComponent>
    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
    private lateinit var mMutualDestroy: ComponentMapper<MutualDestroyComponent>

    private lateinit var sAttackCircle: AttackCircleSystem

    init {
        world.inject(this)
    }

    override fun startAnimation() {
        var enemyId = 0
        world.fetch(allOf(EnemyComponent::class)).forEach {
            val cEnemy = mEnemy.get(it)
            if (cEnemy.enemy == enemy) {
                enemyId = it
            }
        }
        val cEnemyXy = mXy.get(enemyId)

        val id = world.create()
        val cAttackCircle = mAttackCircle.create(id)
        cAttackCircle.setByCountdown(attack)
        sAttackCircle.handleNewAttackCircle(id)
        val index = sAttackCircle.getCircleIndex(attack.enemyId, attack.id)
        mXy.create(id).setXy(cEnemyXy.x + 64f * index, cEnemyXy.y - 100f)
        val angularIncrement = 360f / cAttackCircle.maxTurns
        val cRadial = mRadial.create(id)
        cRadial.sprite = RadialSprite(game.skin.getRegion("circle"))
        cRadial.sprite.setAngle(angularIncrement * (cAttackCircle.maxTurns + 1 - cAttackCircle.turnsLeft))
        cRadial.sprite.setColor(cAttackCircle.color)

        val bgId = world.create()
        mXy.create(bgId).setXy(cEnemyXy.x + 64f * index, cEnemyXy.y - 100f)
        val cBgRadial = mRadial.create(bgId)
        cBgRadial.sprite = RadialSprite(game.skin.getRegion("circle"))
        cBgRadial.sprite.setAngle(0f)
        cBgRadial.sprite.setColor(Color(1f, 1f, 1f, 0.2f))

        mMutualDestroy.create(id).ids.add(bgId)

        endAnimation(30)
    }

}