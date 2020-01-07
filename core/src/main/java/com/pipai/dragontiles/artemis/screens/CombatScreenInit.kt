package com.pipai.dragontiles.artemis.screens

import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.Tags
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.EnemyClickEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverEnterEvent
import com.pipai.dragontiles.artemis.events.EnemyHoverExitEvent
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.utils.enemyAssetPath

@Wire
class CombatScreenInit(private val game: DragonTilesGame, private val world: World, private val encounter: Encounter) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mHoverable: ComponentMapper<HoverableComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>

    private lateinit var sTags: TagManager

    private lateinit var sController: CombatControllerSystem
    private lateinit var sUi: CombatUiSystem

    init {
        world.inject(this)
    }

    fun initialize() {
        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)

        sController.controller.initCombat()

        encounter.enemies.forEach { (enemy, position) ->
            val entityId = world.create()
            val cXy = mXy.create(entityId)
            cXy.setXy(position)

            val cSprite = mSprite.create(entityId)
            cSprite.sprite = Sprite(game.assets.get(enemyAssetPath(enemy.assetName), Texture::class.java))

            val cEnemy = mEnemy.create(entityId)
            cEnemy.setByEnemy(enemy)

            val cHover = mHoverable.create(entityId)
            cHover.enterEvent = EnemyHoverEnterEvent(cEnemy)
            cHover.exitEvent = EnemyHoverExitEvent()

            mClickable.create(entityId).eventGenerator = { EnemyClickEvent(entityId, it) }
        }
    }

}
