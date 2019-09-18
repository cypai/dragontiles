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
    private val mAttackCircle by mapper<AttackCircleComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mLine by mapper<LineComponent>()

    private val batch = game.spriteBatch

    override fun processSystem() {
        batch.color = Color.WHITE
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
        world.fetch(allOf(XYComponent::class, TileComponent::class))
                .forEach {
                    val region = game.tileSkin.regionFor(mTile.get(it)!!.tile)
                    val cXy = mXy.get(it)
                    batch.draw(region, cXy.x, cXy.y)
                }
        world.fetch(allOf(XYComponent::class, SpriteComponent::class))
                .forEach {
                    val sprite = mSprite.get(it).sprite
                    val cXy = mXy.get(it)
                    sprite.x = cXy.x
                    sprite.y = cXy.y
                    sprite.draw(batch)
                }
        world.fetch(allOf(XYComponent::class, SpriteComponent::class, EnemyComponent::class))
                .forEach {
                    val cEnemy = mEnemy.get(it)
                    val cXy = mXy.get(it)
                    game.smallFont.draw(batch, "${cEnemy.name}   ${cEnemy.hp}/${cEnemy.hpMax}", cXy.x, cXy.y - 4f)
                }
        world.fetch(allOf(XYComponent::class, AttackCircleComponent::class))
                .forEach {
                    val cXy = mXy.get(it)
                    val cAttackCircle = mAttackCircle.get(it)
                    game.font.draw(batch, cAttackCircle.baseDamage.toString(), cXy.x + 26f, cXy.y + 36f)
                }
        batch.end()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        batch.begin()
        world.fetch(allOf(XYComponent::class, RadialSpriteComponent::class))
                .forEach {
                    val cXy = mXy.get(it)
                    val cRadial = mRadial.get(it)
                    cRadial.sprite.draw(batch, cXy.x, cXy.y, cRadial.sprite.getAngle())
                }
        batch.end()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        game.shapeRenderer.begin()
        world.fetch(allOf(LineComponent::class))
                .forEach {
                    val cLine = mLine.get(it)
                    game.shapeRenderer.line(cLine.start.x, cLine.start.y, cLine.end.x, cLine.end.y, cLine.color, cLine.color)
                }
        game.shapeRenderer.end()
    }

}
