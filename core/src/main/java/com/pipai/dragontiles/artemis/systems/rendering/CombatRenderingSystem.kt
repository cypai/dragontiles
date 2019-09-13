package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper

class CombatRenderingSystem(private val game: DragonTilesGame) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mTile by mapper<TileComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mRadial by mapper<RadialSpriteComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<LineComponent>()

    override fun processSystem() {
        game.spriteBatch.color = Color.WHITE
        game.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        game.spriteBatch.begin()
        world.fetch(allOf(XYComponent::class, TileComponent::class))
                .forEach {
                    val region = game.tileSkin.regionFor(mTile.get(it)!!.tile)
                    val cXy = mXy.get(it)
                    game.spriteBatch.draw(region, cXy.x, cXy.y)
                }
        world.fetch(allOf(XYComponent::class, SpriteComponent::class))
                .forEach {
                    val sprite = mSprite.get(it).sprite
                    val cXy = mXy.get(it)
                    sprite.x = cXy.x
                    sprite.y = cXy.y
                    sprite.draw(game.spriteBatch)
                }
        world.fetch(allOf(XYComponent::class, SpriteComponent::class, EnemyComponent::class))
                .forEach {
                    val cEnemy = mEnemy.get(it)
                    val cXy = mXy.get(it)
                    game.smallFont.draw(game.spriteBatch, "${cEnemy.name}   ${cEnemy.hp}/${cEnemy.hpMax}", cXy.x, cXy.y - 4f)
                }
        game.spriteBatch.end()
        game.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        game.spriteBatch.begin()
        world.fetch(allOf(XYComponent::class, RadialSpriteComponent::class))
                .forEach {
                    val cXy = mXy.get(it)
                    val cRadial = mRadial.get(it)
                    cRadial.sprite.draw(game.spriteBatch, cXy.x, cXy.y, cRadial.sprite.getAngle())
                }
        game.spriteBatch.end()
        game.spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        game.shapeRenderer.begin()
        world.fetch(allOf(LineComponent::class))
                .forEach {
                    val cLine = mLine.get(it)
                    game.shapeRenderer.line(cLine.start.x, cLine.start.y, cLine.end.x, cLine.end.y, cLine.color, cLine.color)
                }
        game.shapeRenderer.end()
    }

}
