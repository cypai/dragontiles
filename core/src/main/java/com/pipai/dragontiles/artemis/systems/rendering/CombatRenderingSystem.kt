package com.pipai.dragontiles.artemis.systems.rendering

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper

class CombatRenderingSystem(private val game: DragonTilesGame) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mTile by mapper<TileComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mEnemy by mapper<EnemyComponent>()

    override fun processSystem() {
        game.spriteBatch.color = Color.WHITE
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
    }

}
