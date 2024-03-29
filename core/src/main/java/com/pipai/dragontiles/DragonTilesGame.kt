package com.pipai.dragontiles

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectDescriptorLoader
import com.badlogic.gdx.assets.loaders.SkeletonDataLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.MultiDrawable
import com.badlogic.gdx.scenes.scene2d.utils.OffsetDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.esotericsoftware.spine.SkeletonData
import com.esotericsoftware.spine.SkeletonRenderer
import com.kotcrab.vis.ui.VisUI
import com.pipai.dragontiles.artemis.screens.MainMenuScreen
import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.data.GameDataInitializer
import com.pipai.dragontiles.data.GameStrings
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.meta.GameOptions
import com.pipai.dragontiles.meta.Save
import com.pipai.dragontiles.meta.SaveSerializer
import com.pipai.dragontiles.utils.getLogger
import com.talosvfx.talos.runtime.ParticleEffectDescriptor
import com.talosvfx.talos.runtime.render.SpriteBatchParticleRenderer
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.io.File
import java.lang.Exception

class DragonTilesGame(val gameConfig: GameConfig) : Game() {

    private val logger = getLogger()

    companion object {
        const val WORLD_HEIGHT = 9f // 12 inches
        fun worldWidth() = Gdx.graphics.width / (Gdx.graphics.height / WORLD_HEIGHT)
    }

    lateinit var camera: Camera
        private set

    lateinit var viewport: Viewport
        private set

    lateinit var uiCamera: Camera
        private set

    lateinit var uiViewport: Viewport
        private set

    lateinit var spriteBatch: PolygonSpriteBatch
        private set

    lateinit var shapeRenderer: ShapeRenderer
        private set

    lateinit var skeletonRenderer: SkeletonRenderer
        private set

    lateinit var particleRenderer: SpriteBatchParticleRenderer
        private set

    lateinit var heavyFont: BitmapFont
        private set

    lateinit var tinyHeavyFont: BitmapFont
        private set

    lateinit var font: BitmapFont
        private set

    lateinit var largeFont: BitmapFont
        private set

    lateinit var smallFont: BitmapFont
        private set

    lateinit var tinyFont: BitmapFont
        private set

    lateinit var outlinedFont: BitmapFont
        private set

    lateinit var outlinedSmallFont: BitmapFont
        private set

    lateinit var outlinedTinyFont: BitmapFont
        private set

    lateinit var skin: Skin
        private set

    lateinit var dtskin: Skin
        private set

    lateinit var assets: AssetManager
        private set

    lateinit var tileSkin: TileSkin
        private set

    val data: GameData = GameData()

    lateinit var gameStrings: GameStrings
        private set

    private val saveSerializer = SaveSerializer()

    private val saveFileHandle = FileHandle(File("save/save.json"))

    lateinit var save: Save
        private set

    var music: Music? = null

    fun writeSave() {
        saveFileHandle.writeString(saveSerializer.serialize(save), false)
    }

    override fun create() {
        logger.info("Starting Dragon Tiles with the following config settings:")
        logger.info(gameConfig.resolution.toDebugString())
        logger.info("World Units: ${worldWidth()} x $WORLD_HEIGHT")
        camera = OrthographicCamera()
        viewport = ExtendViewport(WORLD_HEIGHT * gameConfig.resolution.aspectRatio, WORLD_HEIGHT, camera)
        uiCamera = OrthographicCamera()
        uiViewport = ScreenViewport(uiCamera)
        GameDataInitializer().init(data)

        if (saveFileHandle.exists()) {
            try {
                save = saveSerializer.deserialize(saveFileHandle.readString())
            } catch (e: Exception) {
                logger.error("Error on load", e)
                // TODO: Show user crash problem instead of nuking the save
                save = Save(null, 0, GameOptions(1f, 1f, mutableListOf()), true)
                writeSave()
            }
        } else {
            save = Save(null, 0, GameOptions(1f, 1f, mutableListOf()), true)
            writeSave()
        }

        spriteBatch = PolygonSpriteBatch()
        skeletonRenderer = SkeletonRenderer()
        skeletonRenderer.premultipliedAlpha = true
        particleRenderer = SpriteBatchParticleRenderer()
        particleRenderer.setBatch(spriteBatch)
        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)

        dtskin = Skin(Gdx.files.internal("assets/binassets/dt_skin.json"))

        assets = AssetManager()
        File("assets/binassets/audio/bgm").listFiles()!!
            .forEach { assets.load(it.toString(), Music::class.java) }
        File("assets/binassets/audio/fx").listFiles()!!
            .forEach { assets.load(it.toString(), Sound::class.java) }
        File("assets/binassets/graphics/bgs").listFiles()!!
            .filter { it.toString().endsWith("png") }
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/intents").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/status").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/upgrades").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/relics").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/potions").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        assets.load("assets/binassets/graphics/tiles/tiles.png", Texture::class.java)
        assets.load("assets/binassets/graphics/heros/elementalist.png", Texture::class.java)
        File("assets/binassets/graphics/textures").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/enemies").listFiles()!!
            .forEach { assets.load(it.toString(), Texture::class.java) }
        File("assets/binassets/graphics/atlas").listFiles()!!
            .filter { it.toString().endsWith("atlas") }
            .forEach { assets.load(it.toString(), TextureAtlas::class.java) }
        assets.setLoader(
            ParticleEffectDescriptor::class.java,
            ParticleEffectDescriptorLoader(InternalFileHandleResolver())
        )
        File("assets/binassets/particles").listFiles()!!
            .filter { it.toString().endsWith("p") }
            .forEach {
                assets.load(
                    it.toString(),
                    ParticleEffectDescriptor::class.java,
                    ParticleEffectDescriptorLoader.ParticleEffectDescriptorParameter("assets/binassets/graphics/atlas/particles.atlas")
                )
            }
        assets.setLoader(SkeletonData::class.java, SkeletonDataLoader(InternalFileHandleResolver()))
        File("assets/binassets/spine/").listFiles()!!
            .forEach { dir ->
                val name = dir.name
                val atlasName = "assets/binassets/spine/$name/$name.atlas"
                assets.load(atlasName, TextureAtlas::class.java)
                assets.load(
                    "assets/binassets/spine/$name/$name.json",
                    SkeletonData::class.java,
                    SkeletonDataLoader.SkeletonDataParameter(atlasName)
                )
            }
        assets.finishLoading()
        tileSkin = TileSkin(assets.get("assets/binassets/graphics/tiles/tiles.png", Texture::class.java))

        val fontGenerator =
            FreeTypeFontGenerator(Gdx.files.internal("assets/binassets/fonts/Comme-Regular.ttf"))
        val heavyFontGenerator =
            FreeTypeFontGenerator(Gdx.files.internal("assets/binassets/fonts/Comme-Heavy.ttf"))
        val fontParameter = FreeTypeFontParameter()
        val outlinedSize = (gameConfig.resolution.height * 0.03f).toInt()
        val largeSize = (gameConfig.resolution.height * 0.06f).toInt()
        val smallSize = (gameConfig.resolution.height * 0.025f).toInt()
        val tinySize = (gameConfig.resolution.height * 0.017f).toInt()
        fontParameter.size = outlinedSize
        font = fontGenerator.generateFont(fontParameter)

        fontParameter.size = largeSize
        largeFont = fontGenerator.generateFont(fontParameter)

        fontParameter.size = smallSize
        smallFont = fontGenerator.generateFont(fontParameter)

        fontParameter.size = tinySize
        tinyFont = fontGenerator.generateFont(fontParameter)

        fontParameter.borderWidth = 1f
        fontParameter.size = outlinedSize
        outlinedFont = fontGenerator.generateFont(fontParameter)

        fontParameter.size = smallSize
        outlinedSmallFont = fontGenerator.generateFont(fontParameter)

        fontParameter.size = tinySize
        outlinedTinyFont = fontGenerator.generateFont(fontParameter)

        fontParameter.size = outlinedSize
        fontParameter.borderWidth = 2f
        heavyFont = heavyFontGenerator.generateFont(fontParameter)

        fontParameter.size = tinySize
        fontParameter.borderWidth = 1f
        tinyHeavyFont = heavyFontGenerator.generateFont(fontParameter)

        fontGenerator.dispose()

        gameStrings = GameStrings()
        gameStrings.load(Gdx.files.internal("assets/data/heros.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/keywords.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/status.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/spells.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/relics.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/enemies.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/upgrades.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/tiles.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/potions.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/encounters.yml").readString())
        gameStrings.load(Gdx.files.internal("assets/data/text.yml").readString())
        gameStrings.loadEvent(Gdx.files.internal("assets/data/events.yml").readString())

        ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE)

        initSkin()

        setScreen(MainMenuScreen(this))
    }

    private fun initSkin() {
        VisUI.load()

        skin = Skin()

        skin.add("plains", assets.get("assets/binassets/graphics/bgs/plains.png"))

        val card = Texture(Gdx.files.local("assets/binassets/graphics/textures/card.png"))
        skin.add("card", card)

        val circle = Texture(Gdx.files.local("assets/binassets/graphics/textures/circle.png"))
        skin.add("circle", circle)

        val bgTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/paper.jpg"))
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        skin.add("bg", bgTexture)

        val pixmap = Pixmap(1, 1, Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        val disabledDrawable = skin.newDrawable("white", Color(0.1f, 0.1f, 0.1f, 0.5f))
        skin.add("disabled", disabledDrawable, Drawable::class.java)
        val poweredDrawable = skin.newDrawable("white", Color(1f, 1f, 1f, 0.8f))
        skin.add("powered", poweredDrawable, Drawable::class.java)
        val shockedDrawable = skin.newDrawable("white", Color(0.2f, 0.2f, 0f, 0.8f))
        skin.add("shocked", shockedDrawable, Drawable::class.java)

        val frameTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/frame.png"))
        val framePatch = NinePatch(frameTexture, 5, 5, 5, 5)
        skin.add("frameTexture", frameTexture)
        skin.add("frame", framePatch)
        val frameDrawable = MultiDrawable(
            arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")
            )
        )
        skin.add("frameDrawable", frameDrawable, Drawable::class.java)
        val frameDrawableDark = MultiDrawable(
            arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                OffsetDrawable(disabledDrawable, 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")
            )
        )
        skin.add("frameDrawableDark", frameDrawableDark, Drawable::class.java)
        val frameDrawableLight = MultiDrawable(
            arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                OffsetDrawable(shockedDrawable, 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")
            )
        )
        skin.add("frameDrawableLight", frameDrawableLight, Drawable::class.java)
        val frameDrawableShocked = MultiDrawable(
            arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                OffsetDrawable(shockedDrawable, 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")
            )
        )
        skin.add("frameDrawableShocked", frameDrawableShocked, Drawable::class.java)
        val targetOutlineTexture =
            Texture(Gdx.files.local("assets/binassets/graphics/textures/target_outline_gray.png"))
        val targetOutlinePatch = NinePatch(targetOutlineTexture, 5, 5, 5, 5)
        val targetOutlineWhiteTexture =
            Texture(Gdx.files.local("assets/binassets/graphics/textures/target_outline_white.png"))
        val targetOutlineWhitePatch = NinePatch(targetOutlineWhiteTexture, 5, 5, 5, 5)
        skin.add("targetOutlineGray", targetOutlinePatch)
        skin.add("targetOutlineWhite", targetOutlineWhitePatch)

        val flatFrameTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/flatframe.png"))
        val flatFramePatch = NinePatch(flatFrameTexture, 5, 5, 5, 5)
        skin.add("flatFrameTexture", flatFrameTexture)
        skin.add("flatFrame", flatFramePatch)
        val flatFrameDrawable = MultiDrawable(
            arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                skin.getDrawable("flatFrame")
            )
        )
        skin.add("flatFrameDrawable", flatFrameDrawable, Drawable::class.java)

        val glowFrameTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/glow_frame.png"))
        val glowFramePatch = NinePatch(glowFrameTexture, 5, 5, 5, 5)
        skin.add("glowFrameTexture", glowFrameTexture)
        skin.add("glowFramePatch", glowFramePatch)

        val transparencyBgTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/transparencyBg.png"))
        skin.add("transparencyBg", transparencyBgTexture)

        val whiteDrawable = skin.newDrawable("white", Color.WHITE)
        val grayDrawable = skin.newDrawable("white", Color.LIGHT_GRAY)
        val blackDrawable = skin.newDrawable("white", Color.BLACK)

        skin.add("cardReq", LabelStyle(heavyFont, Color.WHITE))
        skin.add("cardReqRed", LabelStyle(heavyFont, Color.RED))
        skin.add("cardReqTiny", LabelStyle(tinyHeavyFont, Color.WHITE))
        skin.add("default", LabelStyle(font, Color.BLACK))
        skin.add("small", LabelStyle(smallFont, Color.BLACK))
        skin.add("tiny", LabelStyle(tinyFont, Color.BLACK))
        skin.add("white", LabelStyle(outlinedFont, Color.WHITE))
        skin.add("whiteSmall", LabelStyle(outlinedSmallFont, Color.WHITE))
        skin.add("whiteTiny", LabelStyle(outlinedTinyFont, Color.WHITE))
        val devLabelStyle = LabelStyle(font, Color.BLACK)
        devLabelStyle.background = whiteDrawable
        skin.add("dev", devLabelStyle)

        val textFieldStyle = TextFieldStyle(
            font, Color.BLACK,
            blackDrawable, grayDrawable, whiteDrawable
        )
        skin.add("default", textFieldStyle)

        val smallTextFieldStyle = TextFieldStyle(
            smallFont, Color.BLACK,
            blackDrawable, grayDrawable, whiteDrawable
        )
        skin.add("small", smallTextFieldStyle)

        val textButtonStyle = TextButton.TextButtonStyle(frameDrawable, flatFrameDrawable, frameDrawable, smallFont)
        textButtonStyle.fontColor = Color.BLACK
        skin.add("default", textButtonStyle)

        val clearGrayDrawable = skin.newDrawable("white", Color(0.5f, 0.5f, 0.5f, 0.5f))
        val clearDarkGrayDrawable = skin.newDrawable("white", Color(0.3f, 0.3f, 0.3f, 0.5f))

        val listStyle = List.ListStyle(smallFont, Color.BLACK, Color.BLACK, grayDrawable)
        listStyle.background = whiteDrawable
        skin.add("default", listStyle)
        val scrollPaneStyle = ScrollPane.ScrollPaneStyle()
        scrollPaneStyle.vScroll = OffsetDrawable(clearGrayDrawable, -8f, 0f, 6f, 0f)
        scrollPaneStyle.vScrollKnob = OffsetDrawable(clearDarkGrayDrawable, -8f, 0f, 6f, 0f)
        skin.add("default", scrollPaneStyle)
        val splitPaneStyle = SplitPane.SplitPaneStyle(grayDrawable)
        skin.add("default-horizontal", splitPaneStyle)
        val selectBoxStyle = SelectBox.SelectBoxStyle(smallFont, Color.BLACK, whiteDrawable, scrollPaneStyle, listStyle)
        skin.add("default", selectBoxStyle)

        val menuListStyle = List.ListStyle(
            font, Color.BLACK, Color.BLACK,
            OffsetDrawable(clearGrayDrawable, 4f, 4f, -8f, -8f)
        )
        menuListStyle.background = frameDrawable
        skin.add("menuList", menuListStyle)
        val smallMenuListStyle = List.ListStyle(
            smallFont, Color.BLACK, Color.BLACK,
            OffsetDrawable(skin.newDrawable("white", Color(0.5f, 0.5f, 0.5f, 0.5f)), 4f, 1f, -16f, -2f)
        )
        skin.add("smallMenuList", smallMenuListStyle)

        val windowStyle = Window.WindowStyle(smallFont, Color.BLACK, frameDrawable)
        skin.add("default", windowStyle)
    }

    override fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
        font.dispose()
        screen.dispose()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        uiViewport.update(width, height, true)
    }

    override fun setScreen(screen: Screen) {
        if (this.screen != null) {
            this.screen.dispose()
        }
        logger.debug("Switching Gui to " + screen.javaClass)
        super.setScreen(screen)
    }
}
