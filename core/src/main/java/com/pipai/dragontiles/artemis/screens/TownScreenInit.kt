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
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose

@Wire
class TownScreenInit(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val world: World,
    private val init: Boolean
) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>

    private lateinit var sTags: TagManager

    init {
        world.inject(this)
    }

    private fun initTown() {
        val spellShop = SpellShop(
            runData.hero.heroClass.getRandomClassSpells(runData, 3).map { pricedSpell(it) }.toMutableList(),
            mutableListOf(),
            pricedSpell(GameData.colorlessSpells.filter { it.rarity != Rarity.SPECIAL }.choose(runData.rng)),
        )
        val itemShop = ItemShop(mutableListOf())
        val scribe = Scribe(mutableListOf())
        runData.town = Town(3, null, spellShop, itemShop, scribe)
    }

    private fun pricedSpell(spell: Spell): PricedSpell {
        val price = when (spell.rarity) {
            Rarity.COMMON -> 2
            Rarity.UNCOMMON -> 3
            Rarity.RARE -> 4
            else -> 0
        }
        return PricedSpell(spell, price)
    }

    fun initialize() {
        if (init) {
            initTown()
        }

        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)

        val innId = world.create()
        mXy.create(innId).setXy(80f, 200f)
        mSprite.create(innId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/inn.png", Texture::class.java))

        val shopId = world.create()
        mXy.create(shopId).setXy(400f, 200f)
        mSprite.create(shopId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/shop.png", Texture::class.java))
        mClickable.create(shopId).eventGenerator = { ShopClickEvent() }

        val scribeId = world.create()
        mXy.create(scribeId).setXy(720f, 200f)
        mSprite.create(scribeId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/scribe.png", Texture::class.java))

        val solicitId = world.create()
        mXy.create(solicitId).setXy(80f, 20f)
        mSprite.create(solicitId).sprite =
            Sprite(game.assets.get("assets/binassets/graphics/textures/solicit.png", Texture::class.java))
    }

}
