package com.pipai.dragontiles.artemis.screens

import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.Tags
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.misc.RadialSprite
import com.pipai.dragontiles.utils.enemyAssetPath

@Wire
class CombatScreenInit(private val game: DragonTilesGame, private val world: World) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mRadial: ComponentMapper<RadialSpriteComponent>
    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
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

        sController.controller.api.spellInstances.forEachIndexed { index, spellInstance ->
            sUi.setSpell(index + 1, spellInstance)
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

        sController.controller.runTurn()

        rad(90f, Color(1f, 0f, 0f, 0.9f), false)
        rad(0f, Color(1f, 1f, 1f, 0.2f), true)
    }

    private fun rad(angle: Float, color: Color, bg: Boolean) {
        val radialId = world.create()
        mXy.create(radialId).setXy(100f, 180f)
        val cRadial = mRadial.create(radialId)
        cRadial.sprite = RadialSprite(game.skin.getRegion("circle"))
        cRadial.sprite.setAngle(angle)
        cRadial.sprite.setColor(color)
        if (!bg) {
            val cAttackCircle = mAttackCircle.create(radialId)
            cAttackCircle.color = color
            cAttackCircle.baseDamage = 10
            cAttackCircle.turnsLeft = 3
        }
    }

}
