package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.AttackCircleClickEvent
import com.pipai.dragontiles.artemis.events.AttackCircleHoverEnterEvent
import com.pipai.dragontiles.artemis.events.AttackCircleHoverExitEvent
import com.pipai.dragontiles.combat.CountdownAttack
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.misc.RadialSprite
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class CreateAttackCircleAnimation(private val enemy: Enemy,
                                  private val attack: CountdownAttack) : Animation() {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mRadial: ComponentMapper<RadialSpriteComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>
    private lateinit var mHoverable: ComponentMapper<HoverableComponent>
    private lateinit var mMutualDestroy: ComponentMapper<MutualDestroyComponent>
    private lateinit var mTextLabel: ComponentMapper<TextLabelComponent>

    override fun startAnimation() {
        val enemyId = world.fetch(allOf(EnemyComponent::class))
                .first { mEnemy.get(it).enemy.id == enemy.id }
        val cEnemyXy = mXy.get(enemyId)

        val id = world.create()
        val cAttackCircle = mAttackCircle.create(id)
        cAttackCircle.enemyId = enemy.id
        cAttackCircle.setByCountdown(attack)
        val cCircleXy = mXy.create(id)
        cCircleXy.setXy(cEnemyXy.x + 64f, cEnemyXy.y - 100f)
        val cRadial = mRadial.create(id)
        cRadial.sprite = RadialSprite(game.skin.getRegion("circle"))
        cRadial.sprite.setAngle(360f)
        cRadial.sprite.setColor(cAttackCircle.color)

        val bgId = world.create()
        mXy.create(bgId).setXy(cEnemyXy.x + 64f, cEnemyXy.y - 100f)
        val cBgRadial = mRadial.create(bgId)
        cBgRadial.sprite = RadialSprite(game.skin.getRegion("circle"))
        cBgRadial.sprite.setAngle(0f)
        cBgRadial.sprite.setColor(Color(1f, 1f, 1f, 0.2f))

        val md = mMutualDestroy.create(id)
        md.ids.add(bgId)

        val cClick = mClickable.create(id)
        cClick.eventGenerator = { AttackCircleClickEvent(id, it) }
        val cHover = mHoverable.create(id)
        cHover.enterEvent = AttackCircleHoverEnterEvent(cAttackCircle)
        cHover.exitEvent = AttackCircleHoverExitEvent()

        val atk = attack.calcAttackPower()
        val eff = attack.calcEffectPower()
        when {
            atk > 0 && eff > 0 -> {
                addSwordIcon(attack.calcAttackPower(), cAttackCircle, md, cCircleXy.x - 32f, cCircleXy.y + 32f)
                addSpiralIcon(attack.calcEffectPower(), cAttackCircle, md, cCircleXy.x - 32f, cCircleXy.y)
            }
            atk > 0 -> {
                addSwordIcon(attack.calcAttackPower(), cAttackCircle, md, cCircleXy.x - 32f, cCircleXy.y + 16f)
            }
            eff > 0 -> {
                addSpiralIcon(attack.calcEffectPower(), cAttackCircle, md, cCircleXy.x - 32f, cCircleXy.y + 16f)
            }
        }
        addHourglassIcon(attack.turnsLeft, cAttackCircle, md, cCircleXy.x, cCircleXy.y)

        endAnimation(30)
    }

    private fun addSwordIcon(damage: Int, ac: AttackCircleComponent, md: MutualDestroyComponent, x: Float, y: Float) {
        val sword = world.create()
        md.ids.add(sword)
        val cXy = mXy.create(sword)
        cXy.setXy(x, y)
        val cSprite = mSprite.create(sword)
        cSprite.sprite = Sprite(game.assets.get("assets/binassets/graphics/textures/sword.png", Texture::class.java))
        val cTextLabel = mTextLabel.create(sword)
        cTextLabel.text = damage.toString()
        cTextLabel.xOffset = -16f
        cTextLabel.yOffset = 16f
        ac.swordId = sword
    }

    private fun addSpiralIcon(amount: Int, ac: AttackCircleComponent, md: MutualDestroyComponent, x: Float, y: Float) {
        val spiral = world.create()
        md.ids.add(spiral)
        val cXy = mXy.create(spiral)
        cXy.setXy(x, y)
        val cSprite = mSprite.create(spiral)
        cSprite.sprite = Sprite(game.assets.get("assets/binassets/graphics/textures/spiral.png", Texture::class.java))
        val cTextLabel = mTextLabel.create(spiral)
        cTextLabel.text = amount.toString()
        cTextLabel.xOffset = -16f
        cTextLabel.yOffset = 16f
        ac.spiralId = spiral
    }

    private fun addHourglassIcon(time: Int, ac: AttackCircleComponent, md: MutualDestroyComponent, circleX: Float, circleY: Float) {
        val hourglass = world.create()
        md.ids.add(hourglass)
        val cXy = mXy.create(hourglass)
        cXy.setXy(circleX + 64f, circleY + 16f)
        val cSprite = mSprite.create(hourglass)
        cSprite.sprite = Sprite(game.assets.get("assets/binassets/graphics/textures/hourglass.png", Texture::class.java))
        val cTextLabel = mTextLabel.create(hourglass)
        cTextLabel.text = time.toString()
        cTextLabel.xOffset = 32f
        cTextLabel.yOffset = 16f
        ac.hourglassId = hourglass
    }

}
