package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper

class RenderingSystem(
    private val game: DragonTilesGame
) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mDepth by mapper<DepthComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mSpine by mapper<SpineComponent>()
    private val mActor by mapper<ActorComponent>()
    private val mParticle by mapper<ParticleEffectComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()
    private val mLine by mapper<AnchoredLineComponent>()

    private val batch = game.spriteBatch

    override fun processSystem() {
        batch.color = Color.WHITE
        batch.begin()
        world.fetch(allOf(XYComponent::class, SpineComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cSpine = mSpine.get(it)
                cSpine.skeleton.x = cXy.x
                cSpine.skeleton.y = cXy.y
                cSpine.state.update(world.delta)
                cSpine.state.apply(cSpine.skeleton)
                cSpine.skeleton.updateWorldTransform()
                game.skeletonRenderer.draw(batch, cSpine.skeleton)
            }
        batch.end()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
        world.fetch(
            allOf(XYComponent::class).one(
                ActorComponent::class.java,
                SpriteComponent::class.java,
            )
        )
            .sortedByDescending { mDepth.getSafe(it, null)?.depth ?: 0 }
            .forEach {
                val cXy = mXy.get(it)
                val cSprite = mSprite.getSafe(it, null)
                if (cSprite == null) {
                    val cActor = mActor.get(it)
                    cActor.actor.x = cXy.x
                    cActor.actor.y = cXy.y
                    cActor.actor.draw(batch, 1f)
                } else {
                    val sprite = cSprite.sprite
                    sprite.x = cXy.x
                    sprite.y = cXy.y
                    sprite.draw(batch)
                }
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

        world.fetch(allOf(XYComponent::class, ParticleEffectComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cParticle = mParticle.get(it)
                val effect = cParticle.effect
                effect.setPosition(cXy.x, cXy.y - 50f)
                effect.update(world.delta)
                game.particleRenderer.render(effect)
                if (effect.isComplete) {
                    mParticle.remove(it)
                }
            }
        batch.end()

//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

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
