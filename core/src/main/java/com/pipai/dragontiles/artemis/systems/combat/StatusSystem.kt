package com.pipai.dragontiles.artemis.systems.combat

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.BackendId
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper

class StatusSystem(private val game: DragonTilesGame) : NoProcessingSystem() {
    private val mXy by mapper<XYComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mStatus by mapper<StatusComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()

    private val heroStatuses: MutableList<EntityId> = mutableListOf()
    private val enemyStatuses: MutableMap<EntityId, MutableList<BackendId>> = mutableMapOf()

    fun handleHeroStatus(statuses: List<Status>) {
        val cHeroXy = mXy.get(world.fetch(allOf(HeroComponent::class)).first())
        heroStatuses.forEach { world.delete(it) }
        statuses.forEachIndexed { index, s ->
            createStatus(cHeroXy, index, s)
        }
    }

    fun handleEnemyStatus(enemyId: BackendId, statuses: List<Status>) {
        val cEnemyXy = mXy.get(world.fetch(allOf(EnemyComponent::class)).find { mEnemy.get(it).enemy.id == enemyId }!!)
        if (enemyId !in enemyStatuses) {
            enemyStatuses[enemyId] = mutableListOf()
        }
        enemyStatuses[enemyId]!!.forEach { world.delete(it) }
        statuses.forEachIndexed { index, s ->
            createStatus(cEnemyXy, index, s)
        }
    }

    private fun createStatus(cTargetXy: XYComponent, index: Int, status: Status) {
        val eid = world.create()
        val cStatus = mStatus.create(eid)
        cStatus.setByStatus(status)
        val cXy = mXy.create(eid)
        cXy.setXy(cTargetXy.x + 16f * index, cTargetXy.y - 44f)
        val cSprite = mSprite.create(eid)
        cSprite.sprite = Sprite(game.assets.get(status.assetName, Texture::class.java))

        if (status.displayAmount) {
            val cText = mTextLabel.create(eid)
            cText.size = TextLabelSize.TINY
            cText.text = status.amount.toString()
        }
    }
}
