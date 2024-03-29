package com.pipai.dragontiles.gui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs
import com.pipai.dragontiles.utils.firstCapOnly
import com.pipai.dragontiles.utils.su
import com.pipai.dragontiles.utils.upgradeAssetPath

class SpellCard(
    private val game: DragonTilesGame,
    private var spell: Spell?,
    var number: Int?,
    skin: Skin,
    private val api: CombatApi?
) : Table(skin) {

    private val backTable = Table()
    private val cardTable = Table()
    private val frontTable = Table()
    private val reqBorder = Image()
    private val reqImage = Image()
    private val reqNumber = Label("", skin, "cardReq")
    private val scoreImage = Image()
    private val cdImage = Image()
    private val cdNumber = Label("", skin, "cardReqTiny")
    private val fluxImage = Image()
    private val fluxNumber = Label("", skin, "cardReq")
    private val nameLabel = Label("", skin, "tiny")
    private val numberLabel = Label("", skin, "tiny")
    private val spellTypeLabel = Label("", skin, "tiny")
    private val descriptionLabel = Label("", skin, "tiny")
    private val upgradeImages = listOf(Image(), Image(), Image())

    private var glowColor: Color? = null

    private val reqSize = su(0.4f)

    companion object {
        val cardWorldWidth = 2f
        val cardWorldHeight = 2.8f
        val cardWidth = su(cardWorldWidth)
        val cardHeight = su(cardWorldHeight)
    }

    private val keyRegex = "(!\\w+)(\\((\\w+)\\))?(\\[.+])?".toRegex()

    var target: Enemy? = null
    var enabled = true
        private set
    var powered = false
        private set
    var shocked = false
        private set
    var flux: Int = 0
    var fluxMax: Int = 0

    val data: MutableMap<String, Int> = mutableMapOf()

    init {
        nameLabel.setAlignment(Align.left)
        reqNumber.setAlignment(Align.center)
        fluxNumber.setAlignment(Align.center)
        numberLabel.setAlignment(Align.right)
        descriptionLabel.setAlignment(Align.topLeft)
        descriptionLabel.wrap = true

        update()

        val cardContainer = Container(cardTable)
            .pad(su(0.1f))

        val topLevelStack = Stack()
        topLevelStack.add(backTable)
        topLevelStack.add(cardContainer)
        topLevelStack.add(frontTable)
        add(topLevelStack)

        val reqStack = Stack()
        reqStack.add(reqImage)
        reqStack.add(reqBorder)
        reqStack.add(reqNumber)
        frontTable.add(reqStack)
            .prefWidth(reqSize)
            .prefHeight(reqSize)
            .left()
            .top()

        val cdTable = Table()
        cdTable.add(cdImage)
            .size(su(0.225f))
        cdTable.add(cdNumber)

        val cdStack = Stack()
        cdStack.add(cdTable)
        cdStack.add(scoreImage)

        frontTable.add(cdStack)
            .expandX()
            .top()
            .padTop(su(0.02f))
            .size(su(0.225f))

        val fluxStack = Stack()
        fluxStack.add(fluxImage)
        fluxStack.add(fluxNumber)
        frontTable.add(fluxStack)
            .prefWidth(reqSize)
            .prefHeight(reqSize)
            .right()
            .top()

        frontTable.row()
        upgradeImages.forEach {
            frontTable.add()
                .colspan(2)
            frontTable.add(it)
                .prefWidth(su(0.3f))
                .prefHeight(su(0.3f))
                .padTop(su(0.1f))
                .padRight(su(0.1f))
                .top()
                .right()
            frontTable.row()
        }
        frontTable.add()
            .colspan(3)
            .expandY()
        frontTable.row()

        cardTable.background = skin.getDrawable("frameDrawable")
        cardTable.add(nameLabel)
            .padTop(su(0.21f))
            .padBottom(su(0.04f))
            .center()
            .expandX()
        cardTable.row()
        cardTable.add(spellTypeLabel)
            .padTop(su(0.04f))
            .padLeft(su(0.08f))
            .left()
            .top()
        cardTable.row()
        cardTable.add(descriptionLabel)
            .prefWidth(cardWidth)
            .prefHeight(cardHeight - 48f)
            .padTop(su(0.05f))
            .padLeft(su(0.08f))
            .padRight(su(0.2f))
            .top()
        cardTable.row()

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
            SuitGroup.ICE_LIGHTNING -> "assets/binassets/graphics/textures/ice_lightning_circle.png"
            SuitGroup.ARCANE -> "assets/binassets/graphics/textures/arcane_circle.png"
            SuitGroup.ANY_NO_FUMBLE -> "assets/binassets/graphics/textures/any_circle.png"
            SuitGroup.ANY -> "assets/binassets/graphics/textures/any_circle.png"
            SuitGroup.RAINBOW -> "assets/binassets/graphics/textures/rainbow_circle.png"
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
    }

    fun disable() {
        enabled = false
        if (!powered) {
            cardTable.background = skin.getDrawable("frameDrawableDark")
        }
        if (shocked) {
            cardTable.background = skin.getDrawable("frameDrawableShocked")
        }
    }

    fun shock() {
        enabled = false
        shocked = true
        cardTable.background = skin.getDrawable("frameDrawableShocked")
    }

    fun makePowered() {
        powered = true
        cardTable.background = skin.getDrawable("frameDrawableLight")
    }

    fun enable() {
        enabled = true
        shocked = false
        reqNumber.style = game.skin.get("cardReq", Label.LabelStyle::class.java)
        if (!powered) {
            cardTable.background = skin.getDrawable("frameDrawable")
        }
    }

    fun glow(color: Color) {
        glowColor = color
        update()
    }

    fun stopGlow() {
        glowColor = null
        update()
    }

    fun update() {
        numberLabel.setText(number?.toString() ?: "")

        val spell = this.spell
        if (glowColor != null) {
            backTable.background = skin.newDrawable("glowFramePatch", glowColor)
        } else {
            backTable.background = null
        }
        if (spell == null || (spell is StandardSpell && spell.exhausted) || (spell is PowerSpell && spell.powered)) {
            reqBorder.drawable = null
            reqImage.drawable = null
            reqNumber.setText("")
            scoreImage.drawable = null
            cdImage.drawable = null
            cdNumber.setText("")
            fluxImage.drawable = null
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
                reqNumber.setText(spell.requirement.reqAmount.text())
            }
            if (spell.scoreable) {
                scoreImage.drawable = TextureRegionDrawable(
                    game.assets.get(
                        "assets/binassets/graphics/textures/yaku_circle.png",
                        Texture::class.java
                    )
                )
            }
            val cdAspect = spell.aspects.findAs(CountdownAspect::class)
            if (cdAspect != null) {
                val cdAssetName = when (cdAspect.type) {
                    CountdownType.SCORE -> "assets/binassets/graphics/textures/yaku_circle.png"
                    CountdownType.CONSUMED_TILES -> ""
                    CountdownType.NUMERIC_SCORE -> "assets/binassets/graphics/textures/yaku_circle.png"
                    CountdownType.NUMERIC -> ""
                }
                cdImage.drawable = TextureRegionDrawable(game.assets.get(cdAssetName, Texture::class.java))
                val cdNumberPrefix = when (cdAspect.type) {
                    CountdownType.SCORE -> ""
                    CountdownType.CONSUMED_TILES -> ""
                    CountdownType.NUMERIC_SCORE -> "N:"
                    CountdownType.NUMERIC -> "N:"
                }
                cdNumber.setText("$cdNumberPrefix${cdAspect.current}")
            }
            if (spell.aspects.any { it is FluxGainAspect }) {
                fluxImage.drawable = TextureRegionDrawable(
                    game.assets.get(
                        "assets/binassets/graphics/textures/flux_square.png",
                        Texture::class.java
                    )
                )
                fluxNumber.setText(spell.baseFluxGain())
                if (spell.baseFluxGain() + flux >= fluxMax && fluxMax != 0) {
                    disable()
                    fluxNumber.style = game.skin.get("cardReqRed", Label.LabelStyle::class.java)
                } else {
                    fluxNumber.style = game.skin.get("cardReq", Label.LabelStyle::class.java)
                }
            } else {
                fluxImage.drawable = null
            }
            val spellLocalization = game.gameStrings.spellLocalization(spell.id)
            nameLabel.setText(spellLocalization.name)
            spellTypeLabel.setText(
                "${spell.type.toString().firstCapOnly()} - ${
                    spell.rarity.toString().firstCapOnly()
                }"
            )
            var description = spellLocalization.description
            spell.aspects.forEach {
                description = it.adjustDescription(description)
            }
            val adjustedDescription = description.replace(keyRegex) {
                val replacement = if (target == null && it.groupValues[4].isNotEmpty()) {
                    it.groupValues[4]
                } else {
                    val castParams = CastParams(if (target == null) listOf() else listOf(target!!.enemyId))
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
                        val flat = spell.xFlatModifier()
                        val mul = spell.xMultiplier()
                        when (flat) {
                            0 -> {
                                when (mul) {
                                    1 -> "X"
                                    else -> "${mul}X"
                                }
                            }
                            else -> {
                                when (mul) {
                                    1 -> "X+$flat"
                                    else -> "${mul}X+$flat"
                                }
                            }
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
