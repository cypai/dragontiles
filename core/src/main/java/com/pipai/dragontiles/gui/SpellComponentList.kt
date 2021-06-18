package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileSkin

class SpellComponentList(
    private val skin: Skin,
    private val tileSkin: TileSkin
) : ScrollPane(Table(), skin) {

    private val table = actor as Table

    var topText = ""
    private val options: MutableList<List<TileInstance>> = mutableListOf()
    private val optionFilter: MutableList<TileInstance> = mutableListOf()
    private val rows: MutableMap<Int, List<TileInstance>> = mutableMapOf()

    private val clickCallbacks: MutableList<(List<TileInstance>) -> Unit> = mutableListOf()

    init {
        table.background = skin.getDrawable("disabled")

        table.touchable = Touchable.enabled
        table.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val rowHeight = (table.height - 16) / table.rows
                val index = table.rows - (y / rowHeight).toInt()
                clickCallbacks.forEach { it.invoke(rows[index]!!) }
            }
        })
    }

    fun setOptions(options: List<List<TileInstance>>) {
        this.options.clear()
        this.options.addAll(options)
        optionFilter.clear()
        refreshOptions()
    }

    fun filterOptions(tiles: List<TileInstance>) {
        optionFilter.clear()
        optionFilter.addAll(tiles)
        refreshOptions()
    }

    fun refreshOptions() {
        rows.clear()
        table.clearChildren()
        table.left()
        val topLabel = Label(topText, skin, "white")
        table.add(topLabel).colspan(2).height(16f)
        table.row()
        options.filter { it.containsAll(optionFilter) }
            .forEachIndexed { index, option ->
                rows[index + 1] = option
                val label = Label((index + 1).toString(), skin, "white")
                table.add(label)
                val hGroup = HorizontalGroup()
                option.forEach { tile ->
                    hGroup.addActor(Image(tileSkin.regionFor(tile.tile)))
                }
                table.add(hGroup)
                    .height(hGroup.prefHeight)
                table.row()
            }
        table.validate()
        scrollY = 0f
        updateVisualScroll()
    }

    fun addClickCallback(callback: (List<TileInstance>) -> Unit) {
        clickCallbacks.add(callback)
    }

}
