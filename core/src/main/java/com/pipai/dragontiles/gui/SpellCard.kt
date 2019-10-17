package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Spell

class SpellCard(private val game: DragonTilesGame,
                private var spell: Spell?,
                val number: Int?,
                skin: Skin,
                private val api: CombatApi,
                private val sToolTip: TooltipSystem) : Table(skin) {

    private val topRow = Table()
    private val nameLabel = Label("", skin, "small")
    private val reqLabel = Label("", skin, "small")
    private val numberLabel = Label("", skin, "small")
    private val descriptionLabel = Label("", skin, "small")

    private val regex = "!\\w+".toRegex()

    private val clickCallbacks: MutableList<(SpellCard) -> Unit> = mutableListOf()

    var target: Enemy? = null
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

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (spell != null) {
                    sToolTip.addText("Spell Components", spell!!.requirement.description)
                    sToolTip.addKeywordsInString(game.spellStrings.description(spell!!.id))
                    sToolTip.showTooltip(stage)
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                sToolTip.hideTooltip()
            }
        })
    }

    fun addClickCallback(callback: (SpellCard) -> Unit) {
        clickCallbacks.add(callback)
    }

    fun getSpell() = spell

    fun setSpell(spell: Spell?) {
        this.spell = spell
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

        val theSpell = spell
        if (theSpell == null) {
            nameLabel.setText("")
            reqLabel.setText("")
            descriptionLabel.setText("")
        } else {
            val spellStrings = game.spellStrings.all(theSpell.id)
            nameLabel.setText(spellStrings.name + if (theSpell.upgraded) "+" else "")
            reqLabel.setText(theSpell.requirement.reqString)
            val description = if (theSpell.upgraded) spellStrings.upgradeDescription else spellStrings.description
            val adjustedDescription = description.replace(regex) {
                theSpell.dynamicValue(it.value, api, CastParams(if (target == null) listOf() else listOf(target!!))).toString()
            }
            descriptionLabel.setText(adjustedDescription)
        }
    }

}
