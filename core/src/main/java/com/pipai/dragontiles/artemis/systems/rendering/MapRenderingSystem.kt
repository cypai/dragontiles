package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.AnchoredLineComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper

class MapRenderingSystem(game: DragonTilesGame) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mLine by mapper<AnchoredLineComponent>()

    private val batch = game.spriteBatch
    private val shapeRenderer = game.shapeRenderer

    override fun processSystem() {
        batch.color = Color.WHITE
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
        world.fetch(allOf(XYComponent::class, SpriteComponent::class))
                .forEach {
                    val sprite = mSprite.get(it).sprite
                    val cXy = mXy.get(it)
                    sprite.x = cXy.x
                    sprite.y = cXy.y
                    sprite.draw(batch)
                }
        batch.end()
        shapeRenderer.begin()
        world.fetch(allOf(AnchoredLineComponent::class))
                .forEach {
                    val cLine = mLine.get(it)
                    val xy1 = mXy.get(cLine.anchor1).toVector2().add(cLine.anchor1Offset)
                    val xy2 = mXy.get(cLine.anchor2).toVector2().add(cLine.anchor2Offset)
                    shapeRenderer.line(xy1.x, xy1.y, xy2.x, xy2.y, cLine.color, cLine.color)
                }
        shapeRenderer.end()
    }

}
