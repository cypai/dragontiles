package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.TileClickEvent
import com.pipai.dragontiles.artemis.systems.ui.TooltipSystem
import com.pipai.dragontiles.data.Suit
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.gui.CombatUiLayout

abstract class TileAnimation(protected val layout: CombatUiLayout) : Animation() {

    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mTile: ComponentMapper<TileComponent>
    private lateinit var mPath: ComponentMapper<PathInterpolationComponent>
    private lateinit var mDepth: ComponentMapper<DepthComponent>
    private lateinit var mSprite: ComponentMapper<SpriteComponent>
    private lateinit var mClickable: ComponentMapper<ClickableComponent>
    private lateinit var mHoverable: ComponentMapper<HoverableComponent>

    private lateinit var sTooltip: TooltipSystem

    fun createTile(
        tile: TileInstance,
        x: Float,
        y: Float
    ): Int {
        val entityId = world.create()
        val cTile = mTile.create(entityId)
        cTile.tile = tile
        val cSprite = mSprite.create(entityId)
        cSprite.sprite = Sprite(layout.tileSkin.regionFor(tile.tile))
        cSprite.width = layout.tileWidth
        cSprite.height = layout.tileHeight
        when (tile.tileStatus) {
            TileStatus.BURN -> cSprite.sprite.color = Color.SCARLET
            TileStatus.FREEZE -> cSprite.sprite.color = Color.SKY
            TileStatus.SHOCK -> cSprite.sprite.color = Color.YELLOW
            TileStatus.VOLATILE -> cSprite.sprite.color = Color.PINK
            TileStatus.CURSE -> cSprite.sprite.color = Color.GRAY
            else -> {
            }
        }
        mDepth.create(entityId)
        val cXy = mXy.create(entityId)
        cXy.setXy(x, y)
        val cClickable = mClickable.create(entityId)
        cClickable.eventGenerator = { TileClickEvent(entityId, it) }
        val cHoverable = mHoverable.create(entityId)
        cHoverable.enterCallback = {
            cHoverable.recheck = true
            if (tile.tile.suit == Suit.FUMBLE) {
                sTooltip.addKeyword("@FumbleTile")
            }
            when (tile.tileStatus) {
                TileStatus.BURN -> sTooltip.addKeyword("@Burn")
                TileStatus.FREEZE -> sTooltip.addKeyword("@Freeze")
                TileStatus.SHOCK -> sTooltip.addKeyword("@Shock")
                TileStatus.VOLATILE -> sTooltip.addKeyword("@Volatile")
                TileStatus.CURSE -> sTooltip.addKeyword("@Curse")
                else -> {
                }
            }
            sTooltip.showTooltip()
        }
        cHoverable.exitCallback = {
            sTooltip.hideTooltip()
        }
        return entityId
    }

    fun moveTile(entityId: Int, location: Vector2, t: Float, endCallback: (PathInterpolationComponent) -> Unit) {
        val cXy = mXy.get(entityId)
        val cPath = mPath.create(entityId)
        cPath.setPath(cXy.toVector2(), location, t, Interpolation.pow3Out, EndStrategy.REMOVE)
        cPath.onEndpoint = endCallback
    }

    fun moveTile(entityId: Int, location: Vector2, endCallback: (PathInterpolationComponent) -> Unit) {
        moveTile(entityId, location, 0.4f, endCallback)
    }
}
