package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin
import com.pipai.dragontiles.spells.FullCastHand

class SpellComponentList(
    private val skin: Skin,
    private val tileSkin: TileSkin
) : ScrollPane(Table(), skin) {

    private val table = actor as Table

    var topText = ""
    private val options: MutableList<List<TileInstance>> = mutableListOf()
    private val fullCastOptions: MutableList<FullCastHand> = mutableListOf()
    private val optionFilter: MutableList<TileInstance> = mutableListOf()
    private val rows: MutableMap<Int, List<TileInstance>> = mutableMapOf()
    private val fcRows: MutableMap<Int, FullCastHand> = mutableMapOf()

    private val clickCallbacks: MutableList<(List<TileInstance>) -> Unit> = mutableListOf()
    private val sorceryClickCallbacks: MutableList<(FullCastHand) -> Unit> = mutableListOf()

    init {
        table.background = skin.getDrawable("disabled")

        table.touchable = Touchable.enabled
        table.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val index = table.getRow(y)
                if (fullCastOptions.isEmpty()) {
                    if (index in rows) {
                        clickCallbacks.forEach { it.invoke(rows[index]!!) }
                    }
                } else {
                    if (index in fcRows) {
                        sorceryClickCallbacks.forEach { it.invoke(fcRows[index]!!) }
                    }
                }
            }
        })
    }

    fun setOptions(options: List<List<TileInstance>>) {
        fullCastOptions.clear()
        this.options.clear()
        this.options.addAll(options)
        optionFilter.clear()
        refreshOptions()
    }

    fun setFullCastOptions(fullCastHands: List<FullCastHand>) {
        fullCastOptions.clear()
        this.options.clear()
        fullCastHands.forEach { fch ->
            val hand: MutableList<TileInstance> = mutableListOf()
            fch.melds.forEach { hand.addAll(it.tiles) }
            hand.addAll(fch.eye)
            options.add(hand)
            fullCastOptions.add(fch)
        }
        optionFilter.clear()
        refreshOptions()
    }

    fun filterOptions(tiles: List<TileInstance>) {
        optionFilter.clear()
        optionFilter.addAll(tiles)
        refreshOptions()
    }

    fun optionSize(): Int {
        return options.filter { it.containsAll(optionFilter) }.size
    }

    fun refreshOptions() {
        rows.clear()
        table.clearChildren()
        table.left()
            .top()
        val topLabel = Label(topText, skin, "white")
        table.add(topLabel)
            .padLeft(2f)
            .padRight(2f)
            .colspan(2)
            .top()
        table.row()
        var fcIndex = 0
        options.filter { it.containsAll(optionFilter) }
            .forEachIndexed { index, option ->
                rows[index + 1] = option.toList()
                if (fullCastOptions.isNotEmpty()) {
                    fcRows[index + 1] = fullCastOptions[fcIndex]
                    fcIndex++
                }
                val label = Label((index + 1).toString(), skin, "white")
                table.add(label)
                    .top()
                val hGroup = HorizontalGroup()
                option.forEach { tile ->
                    hGroup.addActor(Image(tileSkin.regionFor(tile.tile)))
                }
                table.add(hGroup)
                    .top()
                    .height(hGroup.prefHeight)
                    .padRight(2f)
                table.row()
            }
        table.validate()
        scrollY = 0f
        updateVisualScroll()
    }

    fun addClickCallback(callback: (List<TileInstance>) -> Unit) {
        clickCallbacks.add(callback)
    }

    fun addSorceryClickCallback(callback: (FullCastHand) -> Unit) {
        sorceryClickCallbacks.add(callback)
    }
}
