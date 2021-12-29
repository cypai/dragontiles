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
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.spells.Rarity
import com.pipai.dragontiles.utils.potionAssetPath
import com.pipai.dragontiles.utils.system

class PotionDatabaseUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
) : NoProcessingSystem(), InputProcessor {

    private val topTable = Table()
    private val potionsTable = Table()
    private val scrollPane = ScrollPane(potionsTable)
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

        potionsTable.background = game.skin.getDrawable("disabled")

        val potions = game.data.allPotions()
        addHeader("Common Potions")
        addPotions(potions.filter { it.rarity == Rarity.COMMON })
        addHeader("Uncommon Potions")
        addPotions(potions.filter { it.rarity == Rarity.UNCOMMON })
        addHeader("Rare Potions")
        addPotions(potions.filter { it.rarity == Rarity.RARE })
    }

    private fun addHeader(text: String) {
        potionsTable.add(Label(text, game.skin, "white"))
            .colspan(colspan)
        potionsTable.row()
    }

    private fun addPotions(potions: List<Potion>) {
        var cell: Cell<Image>? = null
        potions.forEachIndexed { i, potion ->
            if (i > 0 && i % colspan == 0) {
                potionsTable.row()
            }
            val image = Image(game.assets.get(potionAssetPath(potion.assetName), Texture::class.java))
            image.addListener(object : ClickListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addLocalized(potion)
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            cell = potionsTable.add(image)
                .size(64f)
        }
        if (cell != null && potions.size % colspan != 0) {
            repeat(colspan - potions.size % colspan) {
                potionsTable.add()
            }
        }
        potionsTable.row()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                game.screen = MainMenuScreen(game, true)
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
