package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.combat.TileTransformedEvent
import com.pipai.dragontiles.gui.CombatUiLayout

class TileTransformAnimation(
    private val ev: TileTransformedEvent,
    layout: CombatUiLayout
) : TileAnimation(layout) {

    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        val entityId = sTileId.getEntityId(ev.previous.id)
        val cXy = mXy.get(entityId)
        world.delete(entityId)
        createTile(ev.tile, cXy.x, cXy.y)
        endAnimation()
    }

}
