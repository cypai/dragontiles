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
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.*

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
    private val nameLabel = Label("", skin, "tiny")
    private val numberLabel = Label("", skin, "tiny")
    private val descriptionLabel = Label("", skin, "tiny")

    private var zPrevious = 0

    companion object {
        val cardWidth = 140f
        val cardHeight = 196f
    }

    private val regex = "(!\\w+)(\\[.+])?".toRegex()

    private val clickCallbacks: MutableList<(InputEvent, SpellCard) -> Unit> = mutableListOf()
    private val hoverEnterCallbacks: MutableList<(SpellCard) -> Unit> = mutableListOf()

    var target: Enemy? = null
    private var enabled = true
    val data: MutableMap<String, Int> = mutableMapOf()

    init {
        setSpell(spell) // Just to update border/req icons
        background = skin.getDrawable("frameDrawable")
        nameLabel.setAlignment(Align.left)
        numberLabel.setAlignment(Align.right)
        descriptionLabel.setAlignment(Align.topLeft)
        descriptionLabel.setWrap(true)
        update()

        val reqStack = Stack()
        reqStack.add(reqImage)
        reqStack.add(reqBorder)
        reqStack.add(reqNumber)
        add(reqStack)
            .prefWidth(32f)
            .prefHeight(32f)
            .padTop(8f)
            .padLeft(8f)
            .left()
        add(nameLabel)
            .left()
            .expandX()
            .padLeft(8f)
        row()
        add(descriptionLabel)
            .prefWidth(cardWidth)
            .prefHeight(cardHeight)
            .padLeft(8f)
            .top()
            .colspan(2)
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

    fun clearHoverCallbacks() {
        hoverEnterCallbacks.clear()
    }

    fun getSpell() = spell

    fun setSpell(spell: Spell?) {
        this.spell = spell
        if (spell == null) {
            reqBorder.drawable = null
            reqImage.drawable = null
            reqNumber.setText("")
        } else {
            reqBorder.drawable = borderDrawable(spell.requirement.type)
            reqImage.drawable = reqSuitDrawable(spell.requirement.suitGroup)
            reqNumber.setText("  " + spell.requirement.reqAmount.text())
        }
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
            SuitGroup.ANY -> "assets/binassets/graphics/textures/any_circle.png"
        }
        return TextureRegionDrawable(game.assets.get(filename, Texture::class.java))
    }

    fun disable() {
        enabled = false
        background = skin.getDrawable("frameDrawableDark")
    }

    fun enable() {
        enabled = true
        background = skin.getDrawable("frameDrawable")
    }

    fun update() {
        numberLabel.setText(number?.toString() ?: "")

        val theSpell = spell
        if (theSpell == null) {
            nameLabel.setText("")
            descriptionLabel.setText("")
        } else {
            val spellLocalization = game.gameStrings.spellLocalization(theSpell.id)
            nameLabel.setText(spellLocalization.name)
            val description = spellLocalization.description
            val adjustedDescription = description.replace(regex) {
                if (target == null && it.groupValues[2].isNotEmpty()) {
                    it.groupValues[2]
                } else {
                    val castParams = CastParams(if (target == null) listOf() else listOf(target!!.id))
                    if (api == null) {
                        theSpell.baseDamage().toString()
                    } else {
                        theSpell.dynamicValue(it.groupValues[1], api, castParams).toString()
                    }
                }
            }.replace("[@\\[\\]]".toRegex(), "")
            descriptionLabel.setText(adjustedDescription)
        }
    }
}
