package com.pipai.dragontiles.artemis.screens

import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.Tags
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.OrthographicCameraComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.utils.enemyAssetPath
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Wire
class CombatScreenInit(private val game: DragonTilesGame, private val world: World) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>

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

        sController.controller.api.spells.forEachIndexed { index, spell ->
            sUi.setSpell(index + 1, spell)
        }

        sController.combat.enemies.forEach {
            val entityId = world.create()
            val cXy = mXy.create(entityId)
            cXy.setXy(92f, 320f)

            val cSprite = mSprite.create(entityId)
            cSprite.sprite = Sprite(game.assets.get(enemyAssetPath(it.assetName), Texture::class.java))

            val cEnemy = mEnemy.create(entityId)
            cEnemy.setByEnemy(it)
        }

        GlobalScope.launch {
            sController.controller.runTurn()
        }
    }

}
