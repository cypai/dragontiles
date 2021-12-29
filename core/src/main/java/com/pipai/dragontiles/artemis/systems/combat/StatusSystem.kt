package com.pipai.dragontiles.artemis.systems.combat

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.BackendId
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.*

class StatusSystem(private val game: DragonTilesGame) : NoProcessingSystem() {
    private val mXy by mapper<XYComponent>()
    private val mEnemy by mapper<EnemyComponent>()
    private val mStatus by mapper<StatusComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mTextLabel by mapper<TextLabelComponent>()
    private val mHoverable by mapper<HoverableComponent>()

    private val sTooltip by system<TooltipSystem>()

    private val heroStatuses: MutableList<EntityId> = mutableListOf()
    private val enemyStatuses: MutableMap<EntityId, MutableList<BackendId>> = mutableMapOf()

    fun handleHeroStatus(statuses: List<Status>) {
        val heroEntityId = world.fetch(allOf(HeroComponent::class)).first()
        heroStatuses.forEach { world.delete(it) }
        heroStatuses.clear()
        statuses.forEachIndexed { index, s ->
            heroStatuses.add(createStatus(heroEntityId, index, s))
        }
    }

    fun handleEnemyStatus(enemyId: BackendId, statuses: List<Status>) {
        world.fetch(allOf(EnemyComponent::class)).find { mEnemy.get(it).enemy.enemyId == enemyId }?.let { enemyEntityId ->
            if (enemyEntityId !in enemyStatuses) {
                enemyStatuses[enemyEntityId] = mutableListOf()
            }
            val list = enemyStatuses[enemyEntityId]!!
            list.forEach { world.delete(it) }
            list.clear()
            statuses.forEachIndexed { index, s ->
                list.add(createStatus(enemyEntityId, index, s))
            }
        }
    }

    fun handleEnemyDefeat(enemyId: EntityId) {
        enemyStatuses[enemyId]?.forEach { world.delete(it) }
        enemyStatuses.remove(enemyId)
    }

    private fun createStatus(target: EntityId, index: Int, status: Status): Int {
        val statusId = world.create()
        val cStatus = mStatus.create(statusId)
        cStatus.setByStatus(status)
        val cTargetXy = mXy.get(target)
        val cTargetSprite = mSprite.get(target)
        val cXy = mXy.create(statusId)
        cXy.setXy(cTargetXy.x + 16f * index, cTargetXy.y + cTargetSprite.sprite.height - 16f)
        val cSprite = mSprite.create(statusId)
        cSprite.sprite = Sprite(game.assets.get(statusAssetPath(status.assetName), Texture::class.java))
        val cHover = mHoverable.create(statusId)
        cHover.enterCallback = {
            cHover.recheck = true
            sTooltip.addNameDescLocalization(game.gameStrings.nameDescLocalization(status.id), allowBlank = true)
            sTooltip.showTooltip()
        }
        cHover.exitCallback = { sTooltip.hideTooltip() }

        if (status.displayAmount) {
            val cText = mTextLabel.create(statusId)
            cText.size = TextLabelSize.TINY
            cText.text = status.amount.toString()
            cText.xOffset = 8f
            cText.yOffset = 8f
        }
        return statusId
    }
}
