package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.combat.TileStatusChangeEvent
import com.pipai.dragontiles.data.TileStatus

class TileStatusChangeAnimation(private val ev: TileStatusChangeEvent) : Animation() {

    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>

    private lateinit var sTileId: TileIdSystem

    override fun startAnimation() {
        ev.tiles.forEach { tile ->
            val entityId = sTileId.getEntityId(tile.id)
            mTile.get(entityId).tile.tileStatus = ev.tileStatus
            mSprite.get(entityId).sprite.color = when (tile.tileStatus) {
                TileStatus.BURN -> Color.SCARLET
                TileStatus.FREEZE -> Color.SKY
                TileStatus.SHOCK -> Color.YELLOW
                TileStatus.VOLATILE -> Color.PINK
                TileStatus.NONE -> Color.WHITE
            }
        }
        endAnimation()
    }

}
