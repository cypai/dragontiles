package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.screens.MainMenuScreen
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.utils.relicAssetPath
import com.pipai.dragontiles.utils.system

class RelicDatabaseUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val topTable = Table()
    private val relicsTable = Table()
    private val scrollPane = ScrollPane(relicsTable)
    private val colspan = 12

    private val sTooltip by system<TooltipSystem>()

    override fun initialize() {
        scrollPane.width = game.gameConfig.resolution.width.toFloat()
        scrollPane.height = game.gameConfig.resolution.height.toFloat() - 40f

        topTable.setFillParent(true)
        topTable.background = game.skin.getDrawable("plains")
        topTable.row()
        topTable.add(scrollPane)
            .colspan(2)
        stage.addActor(topTable)

        stage.scrollFocus = scrollPane

        val relics = game.data.allRelics()
        addHeader("Starter Relics")
        addRelics(relics.filter { it.rarity == Rarity.STARTER })
        addHeader("Common Relics")
        addRelics(relics.filter { it.rarity == Rarity.COMMON })
        addHeader("Uncommon Relics")
        addRelics(relics.filter { it.rarity == Rarity.UNCOMMON })
        addHeader("Rare Relics")
        addRelics(relics.filter { it.rarity == Rarity.RARE })
        addHeader("Special Relics")
        addRelics(relics.filter { it.rarity == Rarity.SPECIAL })
    }

    private fun addHeader(text: String) {
        relicsTable.add(Label(text, game.skin, "white"))
            .colspan(colspan)
        relicsTable.row()
    }

    private fun addRelics(relics: List<Relic>) {
        var cell: Cell<Image>? = null
        relics.forEachIndexed { i, relic ->
            if (i > 0 && i % colspan == 0) {
                relicsTable.row()
            }
            val image = Image(game.assets.get(relicAssetPath(relic.assetName), Texture::class.java))
            image.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addLocalized(relic)
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            cell = relicsTable.add(image)
                .size(64f)
        }
        if (cell != null && relics.size % colspan != 0) {
            repeat(colspan - relics.size % colspan) {
                relicsTable.add()
            }
        }
        relicsTable.row()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                game.screen = MainMenuScreen(game)
                return true
            }
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
