package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector3
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
    private val mText by mapper<TextComponent>()
    private val mLine by mapper<AnchoredLineComponent>()

    private val batch = game.spriteBatch

    override fun processSystem() {
        game.camera.update()
        batch.color = Color.WHITE
        batch.projectionMatrix = game.camera.combined
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
                    if (sprite.color != batch.color) {
                        batch.color = sprite.color
                    }
                    batch.draw(sprite, cXy.x, cXy.y, cSprite.width, cSprite.height)
                }
            }
        batch.color = Color.WHITE

        world.fetch(allOf(XYComponent::class, ParticleEffectComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cParticle = mParticle.get(it)
                val effect = cParticle.effect
                effect.setPosition(cXy.x, cXy.y)
                effect.update(world.delta)
                game.particleRenderer.render(effect)
                if (effect.isComplete) {
                    when (cParticle.endStrategy) {
                        EndStrategy.REMOVE -> mParticle.remove(it)
                        EndStrategy.DESTROY -> world.delete(it)
                        else -> mParticle.remove(it)
                    }
                }
            }
        batch.end()

        game.uiCamera.update()
        batch.color = Color.WHITE
        batch.projectionMatrix = game.uiCamera.combined
        batch.begin()
        world.fetch(allOf(XYComponent::class, TextComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cText = mText.get(it)
                val font = when (cText.size) {
                    TextSize.LARGE -> game.largeFont
                    TextSize.NORMAL -> game.font
                    TextSize.SMALL -> game.smallFont
                    TextSize.TINY -> game.tinyFont
                }
                font.color = cText.color
                val screenXy = game.camera.project(Vector3(cXy.x + cText.xOffset, cXy.y + cText.yOffset, 0f))
                font.draw(batch, cText.text, screenXy.x, screenXy.y)
            }
        batch.end()

        game.shapeRenderer.projectionMatrix = game.camera.combined
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
