package com.pipai.dragontiles.artemis.components

import com.artemis.Component
import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2

class AnchoredLineComponent : Component() {
    var anchor1 = 0
    var anchor1Offset = Vector2()
    var anchor2 = 0
    var anchor2Offset = Vector2()
    var color = Color.BLACK

    fun safeSetAnchor1(thisId: Int, anchorId: Int, mMutualDestroy: ComponentMapper<MutualDestroyComponent>) {
        val cMutualDestroy = mMutualDestroy.create(anchorId)
        cMutualDestroy.ids.add(thisId)
        anchor1 = anchorId
    }

    fun safeSetAnchor2(thisId: Int, anchorId: Int, mMutualDestroy: ComponentMapper<MutualDestroyComponent>) {
        val cMutualDestroy = mMutualDestroy.create(anchorId)
        cMutualDestroy.ids.add(thisId)
        anchor2 = anchorId
    }
}

class MouseFollowComponent : Component()

class MapNodeComponent : Component()

data class PriceComponent(var price: Int = 0) : Component()
