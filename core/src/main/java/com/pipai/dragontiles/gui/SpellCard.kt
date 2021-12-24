package com.pipai.dragontiles.gui

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.upgradeAssetPath

class SpellCard(
    private val game: DragonTilesGame,
    private var spell: Spell?,
    var number: Int?,
    skin: Skin,
    private val api: CombatApi?
) : Table(skin) {

    private val reqBorder = Image()
    private val reqImage = Image()
    private val reqNumber = Label("", skin, "cardReq")
    private val fluxNumber = Label("", skin, "cardReq")
    private val nameLabel = Label("", skin, "tiny")
    private val numberLabel = Label("", skin, "tiny")
    private val spellTypeLabel = Label("", skin, "tiny")
    private val descriptionLabel = Label("", skin, "tiny")
    private val upgradeImages = listOf(Image(), Image(), Image())

    private var zPrevious = 0

    companion object {
        val cardWidth = 154f
        val cardHeight = 216f
    }

    private val keyRegex = "(!\\w+)(\\((\\w+)\\))?(\\[.+])?".toRegex()

    var target: Enemy? = null
    private var enabled = true
    var powered = false
        private set
    var flux: Int = 0
    var fluxMax: Int = 0

    val data: MutableMap<String, Int> = mutableMapOf()

    init {
        background = skin.getDrawable("frameDrawable")
        nameLabel.setAlignment(Align.left)
        numberLabel.setAlignment(Align.right)
        descriptionLabel.setAlignment(Align.topLeft)
        descriptionLabel.wrap = true
        update()

        val reqStack = Stack()
        reqStack.add(reqImage)
        reqStack.add(reqBorder)
        reqStack.add(reqNumber)
        add(reqStack)
            .prefWidth(32f)
            .prefHeight(32f)
            .pad(4f)
            .left()
        add(nameLabel)
            .left()
            .expandX()
        add(fluxNumber)
            .padRight(12f)
            .right()
        row()
        add(spellTypeLabel)
            .padLeft(8f)
            .left()
            .top()
            .colspan(3)
        row()
        add(descriptionLabel)
            .prefWidth(cardWidth)
            .prefHeight(cardHeight - 48f)
            .padLeft(8f)
            .padRight(8f)
            .top()
            .colspan(3)
        row()
        upgradeImages.forEach {
            add(it)
                .prefWidth(32f)
                .prefHeight(32f)
                .pad(8f)
        }
        row()

        width = cardWidth
        height = cardHeight

        touchable = Touchable.enabled
    }

    fun getSpell() = spell

    fun setSpell(spell: Spell?) {
        this.spell = spell
        update()
    }

    private fun borderDrawable(setType: SetType): Drawable {
        val filename = when (setType) {
            SetType.MISC -> "assets/binassets/graphics/textures/misc_border.png"
            SetType.IDENTICAL -> "assets/binassets/graphics/textures/identical_border.png"
            SetType.SEQUENTIAL -> "assets/binassets/graphics/textures/sequential_border.png"
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
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
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
    }

    fun disable() {
        enabled = false
        if (!powered) {
            background = skin.getDrawable("frameDrawableDark")
        }
    }

    fun makePowered() {
        powered = true
        background = skin.getDrawable("frameDrawableLight")
    }

    fun enable() {
        enabled = true
        reqNumber.style = game.skin.get("cardReq", Label.LabelStyle::class.java)
        if (!powered) {
            background = skin.getDrawable("frameDrawable")
        }
    }

    fun update() {
        numberLabel.setText(number?.toString() ?: "")

        val spell = this.spell
        if (spell == null || (spell is StandardSpell && spell.exhausted) || (spell is PowerSpell && spell.powered)) {
            reqBorder.drawable = null
            reqImage.drawable = null
            reqNumber.setText("")
            fluxNumber.setText("")
            nameLabel.setText("")
            spellTypeLabel.setText("")
            descriptionLabel.setText("")
            upgradeImages.forEach { it.drawable = null }
        } else {
            if (spell.requirement is UnplayableRequirement) {
                reqBorder.drawable = null
                reqImage.drawable = null
                reqNumber.setText("")
            } else {
                reqBorder.drawable = borderDrawable(spell.requirement.type)
                reqImage.drawable = reqSuitDrawable(spell.requirement.suitGroup)
                reqNumber.setText("  " + spell.requirement.reqAmount.text())
            }
            if (spell.aspects.any { it is FluxGainAspect }) {
                fluxNumber.setText(spell.baseFluxGain())
                if (spell.baseFluxGain() + flux >= fluxMax && fluxMax != 0) {
                    disable()
                    fluxNumber.style = game.skin.get("cardReqRed", Label.LabelStyle::class.java)
                } else {
                    fluxNumber.style = game.skin.get("cardReq", Label.LabelStyle::class.java)
                }
            }
            val spellLocalization = game.gameStrings.spellLocalization(spell.id)
            nameLabel.setText(spellLocalization.name)
            spellTypeLabel.setText("${spell.type} - ${spell.rarity}")
            var description = spellLocalization.description
            spell.aspects.forEach {
                description = it.adjustDescription(description)
            }
            val adjustedDescription = description.replace(keyRegex) {
                val replacement = if (target == null && it.groupValues[4].isNotEmpty()) {
                    it.groupValues[4]
                } else {
                    val castParams = CastParams(if (target == null) listOf() else listOf(target!!.id))
                    spell.dynamicValue(it.groupValues[1], it.groupValues[3], api, castParams).toString()
                }
                when (it.groupValues[1]) {
                    "!dp" -> {
                        if (replacement == "0") {
                            ""
                        } else {
                            "+ $replacement"
                        }
                    }
                    "!x" -> {
                        val r = replacement.toInt()
                        when {
                            r == 0 -> "X"
                            r < 0 -> "X$r"
                            else -> "X+$r"
                        }
                    }
                    "!xsleft" -> {
                        when (val r = replacement.toInt()) {
                            1 -> ""
                            else -> "$r("
                        }
                    }
                    "!xsright" -> {
                        when (replacement.toInt()) {
                            1 -> ""
                            else -> ")"
                        }
                    }
                    else -> {
                        replacement
                    }
                }
            }.replace("[@\\[\\]]".toRegex(), "")
            descriptionLabel.setText(adjustedDescription)
            spell.getUpgrades().zip(upgradeImages).forEach { (upgrade, img) ->
                img.drawable =
                    TextureRegionDrawable(game.assets.get(upgradeAssetPath(upgrade.assetName), Texture::class.java))
            }
        }
    }
}
