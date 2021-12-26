package com.pipai.dragontiles.artemis.screens

import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.Tags
import com.pipai.dragontiles.artemis.components.ClickableComponent
import com.pipai.dragontiles.artemis.components.OrthographicCameraComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.ShopClickEvent
import com.pipai.dragontiles.data.RunData

@Wire
class TownScreenInit(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val world: World,
) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>

    private lateinit var sTags: TagManager

    init {
        world.inject(this)
    }

    fun initialize() {
        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)

        // Draw actions left

        val innId = world.create()
        mXy.create(innId).setXy(80f, 20f)
        val innSprite = mSprite.create(innId)
        innSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/inn.png", Texture::class.java))
        innSprite.width = 120f
        innSprite.height = 120f

        val spellShopId = world.create()
        mXy.create(spellShopId).setXy(80f, 200f)
        mSprite.create(spellShopId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/spell_shop.png", Texture::class.java))
        mClickable.create(spellShopId).eventGenerator = { ShopClickEvent() }

        val itemShopId = world.create()
        mXy.create(itemShopId).setXy(400f, 200f)
        mSprite.create(itemShopId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/shop.png", Texture::class.java))
        mClickable.create(itemShopId).eventGenerator = { ShopClickEvent() }

        val scribeId = world.create()
        mXy.create(scribeId).setXy(720f, 200f)
        mSprite.create(scribeId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/upgrade_shop.png", Texture::class.java))

        val solicitId = world.create()
        mXy.create(solicitId).setXy(480f, 20f)
        mSprite.create(solicitId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/solicit.png", Texture::class.java))
    }

}
