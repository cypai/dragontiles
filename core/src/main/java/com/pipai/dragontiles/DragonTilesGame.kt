package com.pipai.dragontiles

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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
import com.kotcrab.vis.ui.VisUI
import com.pipai.dragontiles.artemis.screens.CombatScreen
import com.pipai.dragontiles.combat.Combat
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.spells.Invoke
import com.pipai.dragontiles.utils.enemyAssetPath
import com.pipai.dragontiles.utils.getLogger
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import java.util.*

class DragonTilesGame(val gameConfig: GameConfig) : Game() {

    private val logger = getLogger()

    lateinit var spriteBatch: SpriteBatch
        private set

    lateinit var shapeRenderer: ShapeRenderer
        private set

    lateinit var font: BitmapFont
        private set

    lateinit var smallFont: BitmapFont
        private set

    lateinit var skin: Skin
        private set

    lateinit var assets: AssetManager
        private set

    lateinit var tileSkin: TileSkin
        private set

    override fun create() {
        logger.info("Starting Dragon Tiles with the following config settings:")
        logger.info(gameConfig.resolution.toDebugString())

        spriteBatch = SpriteBatch(1000)
        shapeRenderer = ShapeRenderer()

        assets = AssetManager()
        assets.load("assets/binassets/graphics/tiles/tiles.png", Texture::class.java)
        assets.load(enemyAssetPath(FlameTurtle().assetName), Texture::class.java)
        assets.finishLoading()
        tileSkin = TileSkin(assets.get("assets/binassets/graphics/tiles/tiles.png", Texture::class.java))

        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("assets/binassets/graphics/fonts/SourceSansPro-Regular.ttf"))
        val fontParameter = FreeTypeFontParameter()
        fontParameter.size = 20
        font = fontGenerator.generateFont(fontParameter)

        fontParameter.size = 16
        smallFont = fontGenerator.generateFont(fontParameter)
        fontGenerator.dispose()

        ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE)

        initSkin()

        setScreen(CombatScreen(this,
                Combat(Random(),
                        Hero("Elementalist", 80, 80, 15, mutableListOf(Invoke(), Invoke())),
                        mutableListOf(FlameTurtle()))))
    }

    private fun initSkin() {
        VisUI.load()

        skin = Skin()

        Gdx.files.local("assets/binassets/graphics/bgs").list().forEach {
            skin.add(it.nameWithoutExtension(), Texture(it))
        }

        val bgTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/paper.jpg"))
        bgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        skin.add("bg", bgTexture)

        val mainMenuBgTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/mainmenu.jpg"))
        mainMenuBgTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        skin.add("mainMenuBg", mainMenuBgTexture)

        val pixmap = Pixmap(1, 1, Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        val disabledDrawable = skin.newDrawable("white", Color(0.1f, 0.1f, 0.1f, 0.5f))

        val frameTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/frame.png"))
        val framePatch = NinePatch(frameTexture, 5, 5, 5, 5)
        skin.add("frameTexture", frameTexture)
        skin.add("frame", framePatch)
        val frameDrawable = MultiDrawable(arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")))
        skin.add("frameDrawable", frameDrawable, Drawable::class.java)
        val frameDrawableDark = MultiDrawable(arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                OffsetDrawable(disabledDrawable, 1f, 3f, -4f, -4f),
                skin.getDrawable("frame")))
        skin.add("frameDrawableDark", frameDrawableDark, Drawable::class.java)

        val flatFrameTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/flatframe.png"))
        val flatFramePatch = NinePatch(flatFrameTexture, 5, 5, 5, 5)
        skin.add("flatFrameTexture", flatFrameTexture)
        skin.add("flatFrame", flatFramePatch)
        val flatFrameDrawable = MultiDrawable(arrayOf(
                OffsetDrawable(skin.getTiledDrawable("bg"), 1f, 3f, -4f, -4f),
                skin.getDrawable("flatFrame")))
        skin.add("flatFrameDrawable", flatFrameDrawable, Drawable::class.java)

        val transparencyBgTexture = Texture(Gdx.files.local("assets/binassets/graphics/textures/transparencyBg.png"))
        skin.add("transparencyBg", transparencyBgTexture)

        val whiteDrawable = skin.newDrawable("white", Color.WHITE)
        val grayDrawable = skin.newDrawable("white", Color.LIGHT_GRAY)
        val blackDrawable = skin.newDrawable("white", Color.BLACK)

        skin.add("default", LabelStyle(font, Color.BLACK))
        skin.add("small", LabelStyle(smallFont, Color.BLACK))
        val devLabelStyle = LabelStyle(font, Color.BLACK)
        devLabelStyle.background = whiteDrawable
        skin.add("dev", devLabelStyle)

        val textFieldStyle = TextFieldStyle(font, Color.BLACK,
                blackDrawable, grayDrawable, whiteDrawable)
        skin.add("default", textFieldStyle)

        val smallTextFieldStyle = TextFieldStyle(smallFont, Color.BLACK,
                blackDrawable, grayDrawable, whiteDrawable)
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

        val menuListStyle = List.ListStyle(font, Color.BLACK, Color.BLACK,
                OffsetDrawable(clearGrayDrawable, 4f, 4f, -8f, -8f))
        menuListStyle.background = frameDrawable
        skin.add("menuList", menuListStyle)
        val smallMenuListStyle = List.ListStyle(smallFont, Color.BLACK, Color.BLACK,
                OffsetDrawable(skin.newDrawable("white", Color(0.5f, 0.5f, 0.5f, 0.5f)), 4f, 1f, -16f, -2f))
        skin.add("smallMenuList", smallMenuListStyle)

        val windowStyle = Window.WindowStyle(smallFont, Color.BLACK, frameDrawable)
        skin.add("default", windowStyle)
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
        font.dispose()
        screen.dispose()
    }

    override fun setScreen(screen: Screen) {
        if (this.screen != null) {
            this.screen.dispose()
        }
        logger.debug("Switching Gui to " + screen.javaClass)
        super.setScreen(screen)
    }
}
