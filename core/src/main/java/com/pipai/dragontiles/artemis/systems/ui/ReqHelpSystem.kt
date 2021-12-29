package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.spells.SetType
import com.pipai.dragontiles.spells.SuitGroup
import com.pipai.dragontiles.spells.colorless.Invoke

class ReqHelpSystem(private val game: DragonTilesGame, var stage: Stage) : NoProcessingSystem() {

    private val table = Table()
    private val skin = game.skin

    var showing = false
        private set

    override fun initialize() {
        val resolution = game.gameConfig.resolution
        table.background = skin.getDrawable("frameDrawable")

        table.add(Label("Spell Card Explanation", skin))
            .colspan(3)
        table.add(Label("Tile Requirements Explanation", skin))
        table.row()

        val leftTable = Table()

        leftTable.add(Label("Tile requirement ->", skin, "small"))
            .top()
            .right()
        leftTable.row()
        val spellTypeLabel = Label("", skin, "small")
        spellTypeLabel.wrap = true
        val spellTypeText = """
            Spell Types
            
            Attack: Simple attack
            
            Effect: Simple effect
            
            Rune: Can be activated and deactivated once per turn. When active, reserves tile components rather than consumes them. Right click to deactivate.
            
            Power: Can only be played once per combat.
            
            Sorcery: Casted once per matching set during a Sorcery Cast. Enter Sorcery Mode with Spacebar. Sorceries can only be casted when all tiles in your hand are in a set of 3 or at most 1 pair.
            
        """.trimIndent()
        spellTypeLabel.setText(spellTypeText)
        spellTypeLabel.setAlignment(Align.left)
        leftTable.add(spellTypeLabel)
            .padTop(8f)
            .top()
            .left()
            .width(resolution.width / 6f)
        leftTable.row()
        table.add(leftTable)
            .top()
            .left()
            .width(resolution.width / 6f)

        table.add(SpellCard(game, Invoke(), null, skin, null))
            .top()

        table.add(Label("<- Flux requirement", skin, "small"))
            .top()
            .left()

        val reqTable = Table()
        reqTable.background = skin.getDrawable("disabled")
        reqTable.add(Label("Inner Circle: Suit Requirement", skin, "whiteSmall"))
            .colspan(2)
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.ANY)))
        reqTable.add(Label("Any", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.FIRE)))
        reqTable.add(Label("Fire", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.ICE)))
        reqTable.add(Label("Ice", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.LIGHTNING)))
        reqTable.add(Label("Lightning", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.ELEMENTAL)))
        reqTable.add(Label("Fire/Ice/Lightning", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.LIFE)))
        reqTable.add(Label("Life", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.STAR)))
        reqTable.add(Label("Star", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.ARCANE)))
        reqTable.add(Label("Life/Star", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(reqSuitDrawable(SuitGroup.RAINBOW)))
        reqTable.add(Label("Identical sets in Fire/Ice/Lightning", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Label("Border: Set Requirement", skin, "whiteSmall"))
            .colspan(2)
            .padTop(16f)
        reqTable.row()
        reqTable.add(Image(borderDrawable(SetType.MISC)))
        reqTable.add(Label("None", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(borderDrawable(SetType.SEQUENTIAL)))
        reqTable.add(Label("Sequential", skin, "whiteSmall"))
        reqTable.row()
        reqTable.add(Image(borderDrawable(SetType.IDENTICAL)))
        reqTable.add(Label("Identical", skin, "whiteSmall"))
        reqTable.row()
        table.add(reqTable)
            .top()
            .left()
            .padLeft(16f)
        table.x = 0f
        table.y = 64f
        table.width = resolution.width.toFloat()
        table.height = resolution.height * 0.8f
    }

    private fun reqSuitDrawable(suitGroup: SuitGroup): Drawable {
        val filename = when (suitGroup) {
            SuitGroup.FIRE -> "assets/binassets/graphics/textures/fire_circle.png"
            SuitGroup.ICE -> "assets/binassets/graphics/textures/ice_circle.png"
            SuitGroup.LIGHTNING -> "assets/binassets/graphics/textures/lightning_circle.png"
            SuitGroup.LIFE -> "assets/binassets/graphics/textures/life_circle.png"
            SuitGroup.STAR -> "assets/binassets/graphics/textures/star_circle.png"
            SuitGroup.ELEMENTAL -> "assets/binassets/graphics/textures/elemental_circle.png"
            SuitGroup.ARCANE -> "assets/binassets/graphics/textures/arcane_circle.png"
            SuitGroup.ANY_NO_FUMBLE -> "assets/binassets/graphics/textures/any_circle.png"
            SuitGroup.ANY -> "assets/binassets/graphics/textures/any_circle.png"
            SuitGroup.RAINBOW -> "assets/binassets/graphics/textures/rainbow_circle.png"
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
    }

    private fun borderDrawable(setType: SetType): Drawable {
        val filename = when (setType) {
            SetType.MISC -> "assets/binassets/graphics/textures/misc_border.png"
            SetType.IDENTICAL -> "assets/binassets/graphics/textures/identical_border.png"
            SetType.SEQUENTIAL -> "assets/binassets/graphics/textures/sequential_border.png"
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
    }

    fun show() {
        showing = true
        stage.addActor(table)
    }

    fun hide() {
        showing = false
        table.remove()
    }
}
