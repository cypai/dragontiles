package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.data.Keywords
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.data.NameDescLocalization
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.su

class TooltipSystem(private val game: DragonTilesGame, var stage: Stage) : NoProcessingSystem(), InputProcessor {

    private val config = game.gameConfig
    private val gameStrings = game.gameStrings
    private val skin = game.skin

    private val table = Table()

    private val headerSet: MutableSet<String> = mutableSetOf()
    private val textPairs: MutableList<Pair<String, String>> = mutableListOf()

    private var fixX: Float? = null
    private var fixY: Float? = null
    private var mouseX: Float = 0f
    private var mouseY: Float = 0f

    companion object {
        val WIDTH = su(3f)
    }

    fun addNameDescLocalization(nameDescLocalization: NameDescLocalization, allowBlank: Boolean = false) {
        if (allowBlank || nameDescLocalization.description.isNotBlank()) {
            addText(nameDescLocalization.name, nameDescLocalization.description, true)
        }
    }

    fun addText(header: String, text: String, recurse: Boolean) {
        if (header !in headerSet) {
            headerSet.add(header)
            textPairs.add(Pair(header, text.replace("@", "")))
            if (recurse) {
                gameStrings.findKeywords(text)
                    .forEach { addKeyword(it) }
            }
        }
    }

    fun addKeyword(keyword: String) {
        if (keyword !in headerSet) {
            val data = gameStrings.keyword(keyword)
            if (data != null) {
                textPairs.add(Pair(data.name, data.description.replace("@", "")))
                headerSet.add(keyword)
                gameStrings.findKeywords(data.description)
                    .forEach { addKeyword(it) }
            }
        }
    }

    fun addKeywordsInString(str: String) {
        gameStrings.findKeywords(str)
            .forEach { addKeyword(it) }
    }

    fun addLocalized(localized: Localized) {
        val data = gameStrings.nameDescLocalization(localized)
        addNameDescLocalization(data)
        localized.additionalKeywords().forEach { addKeyword(it) }
        localized.additionalLocalized().forEach { addNameDescLocalization(game.gameStrings.nameDescLocalization(it)) }
    }

    fun addSpell(spell: Spell) {
        if (spell.requirement.showTooltip) {
            addText("Requirements", spell.requirement.description, false)
        }
        if (spell is StandardSpell && spell.shockTurns > 0) {
            addText("Shocked", "Cannot be played for ${spell.shockTurns} turns.", false)
        }
        if (spell.aspects.any { it is XAspect }) {
            addKeyword(Keywords.X)
        }
        addKeywordsInString(game.gameStrings.spellLocalization(spell.id).description)
        spell.additionalKeywords().forEach { addKeyword(it) }
        spell.additionalLocalized().forEach { addNameDescLocalization(game.gameStrings.nameDescLocalization(it)) }
        spell.aspects.filterIsInstance<StackableAspect>()
            .forEach { addLocalized(it.status) }
        if (spell.aspects.any { a -> a is ExhaustAspect }) {
            addKeyword(Keywords.EXHAUST)
        }
        if (spell.aspects.any { a -> a is TransformAspect }) {
            addKeyword(Keywords.TRANSFORM)
        }
        if (spell.aspects.any { a -> a is RepeatableAspect || a is LimitedRepeatableAspect }) {
            addKeyword(Keywords.REPEATABLE)
        }
        if (spell.aspects.any { a -> a is FetchAspect }) {
            addKeyword(Keywords.FETCH)
        }
    }

    fun hideTooltip() {
        textPairs.clear()
        headerSet.clear()
        table.clearChildren()
        table.remove()
    }

    fun showTooltip(fixX: Float? = null, fixY: Float? = null) {
        this.fixX = fixX
        this.fixY = fixY
        if (fixX != null && fixX > game.gameConfig.resolution.width - WIDTH) {
            this.fixX = fixX - WIDTH * 2.5f
        }
        if (textPairs.isEmpty()) {
            return
        }
        table.clearChildren()
        table.remove()
        table.background = skin.getDrawable("frameDrawable")
        textPairs.forEach {
            val header = Label(it.first, skin, "small")
            header.setAlignment(Align.topLeft)
            table.add(header)
                .width(WIDTH)
                .padLeft(8f)
                .padRight(8f)
                .left()
            table.row()
            val label = Label(it.second, skin, "tiny")
            label.setAlignment(Align.topLeft)
            label.wrap = true
            table.add(label)
                .width(WIDTH)
                .padLeft(8f)
                .padRight(8f)
                .padBottom(8f)
                .left()
            table.row()
        }
        table.validate()
        table.left().top()
        table.width = table.prefWidth
        table.height = table.prefHeight
        stage.addActor(table)
        table.toFront()
        updateTablePosition()
    }

    private fun updateTablePosition() {
        val resolution = game.gameConfig.resolution
        if (fixX == null) {
            table.x = (mouseX + 16f).coerceAtMost(resolution.width - table.prefWidth)
        } else {
            table.x = fixX!!.coerceAtMost(resolution.width - table.prefWidth)
        }
        if (fixY == null) {
            table.y = (mouseY - table.prefHeight - 16f).coerceAtLeast(0f)
        } else {
            table.y = fixY!!.coerceAtMost(resolution.height - table.prefHeight).coerceAtLeast(0f)
        }
    }

    override fun keyDown(keycode: Int) = false

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        mouseX = screenX.toFloat()
        mouseY = config.resolution.height - screenY.toFloat()
        updateTablePosition()
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float) = false
}
