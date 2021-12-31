package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.combat.AttackIntent
import com.pipai.dragontiles.combat.BuffIntent
import com.pipai.dragontiles.combat.DebuffIntent
import com.pipai.dragontiles.combat.StunnedIntent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.system

class RenderingSystem(
    private val game: DragonTilesGame
) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mDepth by mapper<DepthComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mSpine by mapper<SpineComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()
    private val mLine by mapper<AnchoredLineComponent>()
    private val mTargetHighlight by mapper<TargetHighlightComponent>()

    private val batch = game.spriteBatch

    override fun processSystem() {
        batch.color = Color.WHITE
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
        world.fetch(
            allOf(XYComponent::class).one(
                ActorComponent::class.java,
                SpriteComponent::class.java,
                SpineComponent::class.java,
            )
        )
            .sortedByDescending { mDepth.getSafe(it, null)?.depth ?: 0 }
            .forEach {
                val cXy = mXy.get(it)
                val cSprite = mSprite.getSafe(it, null)
                if (cSprite == null) {
                    val cSpine = mSpine.getSafe(it, null)
                    if (cSpine == null) {
                        val cActor = mActor.get(it)
                        cActor.actor.x = cXy.x
                        cActor.actor.y = cXy.y
                        cActor.actor.draw(batch, 1f)
                    } else {
                        cSpine.skeleton.x = cXy.x
                        cSpine.skeleton.y = cXy.y
                        cSpine.state.update(Gdx.graphics.deltaTime)
                        cSpine.state.apply(cSpine.skeleton)
                        cSpine.skeleton.updateWorldTransform()
                        game.skeletonRenderer.draw(batch, cSpine.skeleton)
                    }
                } else {
                    val sprite = cSprite.sprite
                    sprite.x = cXy.x
                    sprite.y = cXy.y
                    batch.color = sprite.color
                    if (cSprite.width == 0f && cSprite.height == 0f) {
                        sprite.draw(batch)
                    } else {
                        batch.draw(sprite, sprite.x, sprite.y, cSprite.width, cSprite.height)
                    }
                }
            }
        world.fetch(allOf(XYComponent::class, SpineComponent::class))
            .forEach {
            }

        world.fetch(allOf(XYComponent::class, TextLabelComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cTextLabel = mTextLabel.get(it)
//                val font = when (cTextLabel.size) {
//                    TextLabelSize.NORMAL -> game.font
//                    TextLabelSize.SMALL -> game.smallFont
//                    TextLabelSize.TINY -> game.tinyFont
//                }
                val styleName = when (cTextLabel.size) {
                    TextLabelSize.NORMAL -> "white"
                    TextLabelSize.SMALL -> "whiteSmall"
                    TextLabelSize.TINY -> "whiteTiny"
                }
                val label = Label(" ${cTextLabel.text} ", game.skin, styleName)
                label.x = cXy.x + cTextLabel.xOffset
                label.y = cXy.y + cTextLabel.yOffset - label.prefHeight
                label.draw(batch, 1f)
//                font.color = cTextLabel.color
//                font.draw(batch, cTextLabel.text, cXy.x + cTextLabel.xOffset, cXy.y + cTextLabel.yOffset)
            }
        batch.end()


        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        game.shapeRenderer.begin()
        world.fetch(allOf(AnchoredLineComponent::class))
            .forEach {
                val cLine = mLine.get(it)
                val xy1 = mXy.get(cLine.anchor1).toVector2().add(cLine.anchor1Offset)
                val xy2 = mXy.get(cLine.anchor2).toVector2().add(cLine.anchor2Offset)
                game.shapeRenderer.line(xy1.x, xy1.y, xy2.x, xy2.y, cLine.color, cLine.color)
            }
        game.shapeRenderer.end()
    }

}
