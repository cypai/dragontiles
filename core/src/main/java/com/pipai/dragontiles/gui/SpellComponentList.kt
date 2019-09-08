package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileSkin

class SpellComponentList(private val skin: Skin,
                         private val tileSkin: TileSkin) : ScrollPane(Table(), skin) {

    private val table = actor as Table

    private val rows: MutableMap<Int, List<Tile>> = mutableMapOf()

    private val clickCallbacks: MutableList<(List<Tile>) -> Unit> = mutableListOf()

    init {
        table.background = skin.getDrawable("disabled")

        table.touchable = Touchable.enabled
        table.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                val rowHeight = table.height / table.rows
                val index = table.rows - (y / rowHeight).toInt()
                clickCallbacks.forEach { it.invoke(rows[index]!!) }
            }
        })
    }

    fun setOptions(options: List<List<Tile>>) {
        rows.clear()
        table.clearChildren()
        table.left()
        options.forEachIndexed { index, option ->
            rows[index + 1] = option
            val label = Label((index + 1).toString(), skin, "white")
            table.add(label)
            val hGroup = HorizontalGroup()
            option.forEach { tile ->
                hGroup.addActor(Image(tileSkin.regionFor(tile)))
            }
            table.add(hGroup)
                    .height(hGroup.prefHeight)
            table.row()
        }
        table.validate()
        scrollY = 0f
        updateVisualScroll()
    }

    fun addClickCallback(callback: (List<Tile>) -> Unit) {
        clickCallbacks.add(callback)
    }

}
