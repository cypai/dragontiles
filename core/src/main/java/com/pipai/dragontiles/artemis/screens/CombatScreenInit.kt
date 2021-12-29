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
import com.pipai.dragontiles.artemis.systems.ui.DeckDisplayUiSystem
import com.pipai.dragontiles.dungeon.Encounter
import com.pipai.dragontiles.data.RunData
import com.pipai.dragontiles.utils.enemyAssetPath

@Wire
class CombatScreenInit(
    private val game: DragonTilesGame,
    private val world: World,
    private val runData: RunData,
    private val encounter: Encounter
) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mHero: ComponentMapper<HeroComponent>
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mHoverable: ComponentMapper<HoverableComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>
    private lateinit var mTextLabel: ComponentMapper<TextLabelComponent>

    private lateinit var sTags: TagManager

    private lateinit var sController: CombatControllerSystem
    private lateinit var sDeckDisplay: DeckDisplayUiSystem

    init {
        world.inject(this)
    }

    fun initialize() {
        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)

        sDeckDisplay.enableSwap = false

        initHero()

    }

    private fun initHero() {
        val entityId = world.create()
        val cXy = mXy.create(entityId)
        cXy.setXy(100f, 350f)
        val cHero = mHero.create(entityId)
        cHero.setByRunData(runData)
        val cSprite = mSprite.create(entityId)
        cSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/heros/elementalist.png", Texture::class.java))
        val cHeroText = mTextLabel.create(entityId)
        cHeroText.size = TextLabelSize.SMALL
        cHeroText.yOffset = -10f
    }

}
