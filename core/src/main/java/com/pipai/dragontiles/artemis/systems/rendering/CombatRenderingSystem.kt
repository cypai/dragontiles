package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
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

class CombatRenderingSystem(private val game: DragonTilesGame) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mRadial by mapper<RadialSpriteComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()
    private val mHero by mapper<HeroComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<AnchoredLineComponent>()
    private val mTargetHighlight by mapper<TargetHighlightComponent>()

    private val sCombat by system<CombatControllerSystem>()

    private val batch = game.spriteBatch

    override fun processSystem() {
        batch.color = Color.WHITE
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
        world.fetch(allOf(XYComponent::class, SpriteComponent::class))
            .sortedByDescending { mSprite.get(it).depth }
            .forEach {
                val sprite = mSprite.get(it).sprite
                val cXy = mXy.get(it)
                sprite.x = cXy.x
                sprite.y = cXy.y
                sprite.draw(batch)
            }

        world.fetch(allOf(XYComponent::class, TargetHighlightComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cTh = mTargetHighlight.getSafe(it, null)
                if (cTh != null) {
                    val highlight = game.skin.newDrawable("targetOutlineWhite", 1f, 1f, 1f, cTh.alpha)
                    highlight.draw(
                        batch,
                        cXy.x - cTh.padding + cTh.xOffset,
                        cXy.y - cTh.padding,
                        cTh.width + cTh.padding * 2,
                        cTh.height + cTh.padding * 2
                    )
                }
            }

        world.fetch(allOf(XYComponent::class, TextLabelComponent::class))
            .forEach {
                val cXy = mXy.get(it)
                val cTextLabel = mTextLabel.get(it)
                val font = when (cTextLabel.size) {
                    TextLabelSize.NORMAL -> game.font
                    TextLabelSize.SMALL -> game.smallFont
                    TextLabelSize.TINY -> game.tinyFont
                }
                font.color = cTextLabel.color
                font.draw(batch, cTextLabel.text, cXy.x + cTextLabel.xOffset, cXy.y + cTextLabel.yOffset)
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

    private fun elementColor(element: Element?): Color {
        return when (element) {
            Element.FIRE -> Color.RED
            Element.ICE -> Color.SKY
            Element.LIGHTNING -> Color.YELLOW
            else -> Color.WHITE
        }
    }

}
