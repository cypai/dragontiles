package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.spells.Spell

class SpellCard(private var spell: Spell?,
                private var number: Int?,
                skin: Skin,
                private val tileSkin: TileSkin) : Table(skin) {

    private val topRow = Table()
    private val nameLabel = Label("", skin)
    private val numberLabel = Label("", skin)
    private val descriptionLabel = Label("", skin)

    init {
        background = skin.getDrawable("frameDrawable")
        update()

        topRow.add(nameLabel)
                .expand()
        topRow.add(numberLabel)
                .right()
        add(topRow)
                .width(160f)
                .height(24f)
                .pad(8f)
        row()
        add(descriptionLabel)
                .width(160f)
                .height(96f)
        row()
        add()
                .height(64f)
    }

    fun setSpell(spell: Spell?) {
        this.spell = spell
        update()
    }

    private fun update() {
        nameLabel.setText(spell?.name ?: "")
        numberLabel.setText(number?.toString() ?: "")
        descriptionLabel.setText(spell?.description ?: "")
    }

}
