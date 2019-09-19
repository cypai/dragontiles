package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.spells.SpellInstance

class SpellCard(private val game: DragonTilesGame,
                private var spellInstance: SpellInstance?,
                val number: Int?,
                skin: Skin,
                private val tileSkin: TileSkin) : Table(skin) {

    private val topRow = Table()
    private val nameLabel = Label("", skin, "small")
    private val reqLabel = Label("", skin, "small")
    private val numberLabel = Label("", skin, "small")
    private val descriptionLabel = Label("", skin, "small")

    private val regex = "!\\w+".toRegex()

    private val clickCallbacks: MutableList<(SpellCard) -> Unit> = mutableListOf()

    private var enabled = true

    init {
        background = skin.getDrawable("frameDrawable")
        nameLabel.setAlignment(Align.left)
        numberLabel.setAlignment(Align.right)
        descriptionLabel.setAlignment(Align.topLeft)
        descriptionLabel.setWrap(true)
        update()

        topRow.add(nameLabel)
                .expand()
                .left()
        topRow.add(numberLabel)
                .right()
        add(topRow)
                .width(160f)
                .pad(8f)
        row()
        add(reqLabel)
                .left()
                .padLeft(8f)
                .padBottom(8f)
        row()
        add(descriptionLabel)
                .width(160f)
                .height(96f)
                .top()
        row()
        add()
                .height(64f)

        touchable = Touchable.enabled
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (enabled) {
                    clickCallbacks.forEach { it.invoke(this@SpellCard) }
                }
            }
        })
    }

    fun addClickCallback(callback: (SpellCard) -> Unit) {
        clickCallbacks.add(callback)
    }

    fun getSpellInstance() = spellInstance

    fun setSpellInstance(spellInstance: SpellInstance?) {
        this.spellInstance = spellInstance
        update()
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

        val spell = spellInstance?.spell
        if (spell == null) {
            nameLabel.setText("")
            reqLabel.setText("")
            descriptionLabel.setText("")
        } else {
            val spellStrings = game.spellStrings.all(spell.id)
            nameLabel.setText(spellStrings.name + if (spell.upgraded) "+" else "")
            reqLabel.setText(spell.requirement.reqString)
            val description = if (spellInstance!!.spell.upgraded) spellStrings.upgradeDescription else spellStrings.description
            val adjustedDescription = description.replace(regex) {
                spellInstance!!.dynamicValue(it.value).toString()
            }
            descriptionLabel.setText(adjustedDescription)
        }
    }

}