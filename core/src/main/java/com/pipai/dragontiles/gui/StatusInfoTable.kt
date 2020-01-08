package com.pipai.dragontiles.gui

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

class StatusInfoTable(private var hp: Int,
                      private var hpMax: Int,
                      name: String,
                      skin: Skin) : Table(skin) {

    private val nameLabel = Label(name, skin, "tiny")
    private val hpLabel = Label("", skin, "tiny")
    private val hpBar = ProgressBar(0f, hpMax.toFloat(), 1f, false, skin, "redBar")

    private val clickCallbacks: MutableList<(StatusInfoTable) -> Unit> = mutableListOf()

    init {
        background = skin.getDrawable("frameDrawable")
        nameLabel.setAlignment(Align.left)
        update()

        val hpStack = Stack()
        hpStack.addActor(hpBar)
        hpStack.addActor(hpLabel)
        add(hpStack)
                .pad(3f)
                .prefHeight(16f)
                .expandX()
                .left()
        row()
        add(nameLabel)
                .center()
                .padBottom(3f)
        row()
    }

    fun addClickCallback(callback: (StatusInfoTable) -> Unit) {
        clickCallbacks.add(callback)
    }

    fun setHp(hp: Int) {
        this.hp = hp
        update()
    }

    fun setNameText(name: String) {
        nameLabel.setText(name)
    }

    fun update() {
        hpLabel.setText("$hp/$hpMax")
        hpBar.value = hp.toFloat()
    }
}
