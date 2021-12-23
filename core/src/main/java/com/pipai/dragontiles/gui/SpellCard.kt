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
        val cardWidth = 140f
        val cardHeight = 196f
    }

    private val regex = "(!\\w+)(\\[.+])?".toRegex()

    private val clickCallbacks: MutableList<(InputEvent, SpellCard) -> Unit> = mutableListOf()
    private val hoverEnterCallbacks: MutableList<(SpellCard) -> Unit> = mutableListOf()
    private val hoverExitCallbacks: MutableList<(SpellCard) -> Unit> = mutableListOf()

    var target: Enemy? = null
    private var enabled = true
    var powered = false
        private set

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
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (enabled) {
                    clickCallbacks.forEach { it.invoke(event!!, this@SpellCard) }
                }
            }

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (spell != null) {
                    hoverEnterCallbacks.forEach { it.invoke(this@SpellCard) }
//                    zPrevious = zIndex
//                    toFront()
//                    sToolTip.addText("Spell Components", spell!!.requirement.description, true)
//                    sToolTip.addKeywordsInString(game.gameStrings.spellLocalization(spell!!.id).description)
//                    sToolTip.showTooltip(stage)
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                zIndex = zPrevious
                if (spell != null) {
                    hoverExitCallbacks.forEach { it.invoke(this@SpellCard) }
                }
//                sToolTip.hideTooltip()
            }
        })
        addListener(object : ClickListener(Input.Buttons.RIGHT) {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (enabled) {
                    clickCallbacks.forEach { it.invoke(event!!, this@SpellCard) }
                }
            }
        })
    }

    fun addClickCallback(callback: (InputEvent, SpellCard) -> Unit) {
        clickCallbacks.add(callback)
    }

    fun addHoverEnterCallback(callback: (SpellCard) -> Unit) {
        hoverEnterCallbacks.add(callback)
    }

    fun addHoverExitCallback(callback: (SpellCard) -> Unit) {
        hoverExitCallbacks.add(callback)
    }

    fun clearHoverCallbacks() {
        hoverEnterCallbacks.clear()
        hoverExitCallbacks.clear()
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
            reqBorder.drawable = borderDrawable(spell.requirement.type)
            reqImage.drawable = reqSuitDrawable(spell.requirement.suitGroup)
            reqNumber.setText("  " + spell.requirement.reqAmount.text())
            if (spell.aspects.any { it is FluxGainAspect }) {
                fluxNumber.setText(spell.baseFluxGain())
            }
            val spellLocalization = game.gameStrings.spellLocalization(spell.strId)
            nameLabel.setText(spellLocalization.name)
            spellTypeLabel.setText("${spell.type} - ${spell.rarity}")
            var description = spellLocalization.description
            if (spell.aspects.any { it is PostExhaustAspect }) {
                description += " @Exhaust."
            }
            val adjustedDescription = description.replace(regex) {
                val replacement = if (target == null && it.groupValues[2].isNotEmpty()) {
                    it.groupValues[2]
                } else {
                    val castParams = CastParams(if (target == null) listOf() else listOf(target!!.id))
                    if (api == null) {
                        spell.baseDamage().toString()
                    } else {
                        spell.dynamicValue(it.groupValues[1], api, castParams).toString()
                    }
                }
                if (it.groupValues[1] == "!dp") {
                    if (replacement == "0") {
                        ""
                    } else {
                        "+ $replacement"
                    }
                } else {
                    replacement
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
