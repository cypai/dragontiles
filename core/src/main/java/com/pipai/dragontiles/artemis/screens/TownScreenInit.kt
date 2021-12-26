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
import com.pipai.dragontiles.artemis.events.ShopClickEvent
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.data.GlobalApi
import com.pipai.dragontiles.data.RunData
import net.mostlyoriginal.api.event.common.EventSystem

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
    private lateinit var mHoverable: ComponentMapper<HoverableComponent>
    private lateinit var mText: ComponentMapper<TextLabelComponent>

    private lateinit var sTags: TagManager
    private lateinit var sTooltip: TooltipSystem
    private lateinit var sEvent: EventSystem

    init {
        world.inject(this)
    }

    fun initialize() {
        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)

        val town = runData.town!!
        // Draw actions left
        val actionId = world.create()
        mXy.create(actionId).setXy(80f, 600f)
        val cActionText = mText.create(actionId)
        cActionText.text = "Actions: ${town.actions}"

        val innId = world.create()
        mXy.create(innId).setXy(80f, 20f)
        val innSprite = mSprite.create(innId)
        innSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/inn.png", Texture::class.java))
        innSprite.width = 120f
        innSprite.height = 120f

        val spellShopId = world.create()
        mXy.create(spellShopId).setXy(80f, 200f)
        val spellShopSprite = mSprite.create(spellShopId)
        spellShopSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/spell_shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtSpell || town.boughtSideboard) {
            mClickable.create(spellShopId).callback = {
                game.screen = SpellShopScreen(game, runData)
            }
        } else {
            spellShopSprite.sprite.color = Color.LIGHT_GRAY
        }

        val itemShopId = world.create()
        mXy.create(itemShopId).setXy(400f, 200f)
        val itemShopSprite = mSprite.create(itemShopId)
        itemShopSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtItem) {
            mClickable.create(itemShopId).callback = {
                game.screen = ItemShopScreen(game, runData)
            }
        } else {
            itemShopSprite.sprite.color = Color.LIGHT_GRAY
        }

        val scribeId = world.create()
        mXy.create(scribeId).setXy(720f, 200f)
        val scribeSprite = mSprite.create(scribeId)
        scribeSprite.sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/upgrade_shop.png", Texture::class.java))
        if (town.actions > 0 || town.boughtUpgrade) {
            mClickable.create(scribeId).callback = {
                game.screen = ScribeShopScreen(game, runData)
            }
        } else {
            scribeSprite.sprite.color = Color.LIGHT_GRAY
        }

        if (town.actions > 0 && !town.solicited) {
            val solicitId = world.create()
            mXy.create(solicitId).setXy(480f, 20f)
            val solicitSprite = mSprite.create(solicitId)
            solicitSprite.sprite =
                Sprite(game.assets.get("assets/binassets/graphics/textures/solicit.png", Texture::class.java))
            val solicitClick = mClickable.create(solicitId)
            solicitClick.callback = {
                town.actions--
                town.solicited = true
                cActionText.text = "Actions: ${town.actions}"
                GlobalApi(game.data, runData, sEvent).gainGoldImmediate(1)
                world.delete(solicitId)
            }
            val solicitHover = mHoverable.create(solicitId)
            solicitHover.enterCallback = {
                sTooltip.addText("Solicit", "Beg for 1 Gold.", false)
                sTooltip.showTooltip()
            }
            solicitHover.exitCallback = {
                sTooltip.hideTooltip()
            }
        }
    }

}
