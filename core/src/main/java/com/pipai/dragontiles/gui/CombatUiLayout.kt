package com.pipai.dragontiles.gui

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.data.TileSkin

data class CombatUiLayout(val config: GameConfig, val tileSkin: TileSkin, val maxHandSize: Int) {

    val openPoolSize = 9

    val tileWidth = 0.4f
    val tileHeight = 0.6f

    val handCenter = Vector2(DragonTilesGame.worldWidth() / 2f, 2.5f)
    val handBlCorner = Vector2(handCenter.x - tileWidth * maxHandSize / 2f, handCenter.y)
    val drawPosition = Vector2(0f, 0f)
    val discardPosition = Vector2(DragonTilesGame.worldWidth() + 1f, 0f)

    val openPoolCenter = Vector2(DragonTilesGame.worldWidth() / 2f, 3.5f)
    val openBlCorner = Vector2(openPoolCenter.x - tileWidth * openPoolSize / 2f, openPoolCenter.y)

    fun openTilePosition(number: Int) = Vector2(openBlCorner.x + 0.4f * number, openBlCorner.y)
    fun handTilePosition(number: Int) = Vector2(handBlCorner.x + 0.4f * number, handBlCorner.y)
    fun handActiveTilePosition(number: Int) = Vector2(handBlCorner.x + 0.4f * number, handBlCorner.y + tileHeight / 2f)
    fun handRuneTilePosition(tilesInHand: Int, assignedSizes: List<Int>, runeSetIndex: Int, number: Int): Vector2 {
        val start = handTilePosition(tilesInHand)
        start.x += tileWidth / 2f
        val applicableSizeList = assignedSizes.subList(0, runeSetIndex)
        start.x += applicableSizeList.sum() * tileWidth + applicableSizeList.size * tileWidth / 2f
        start.x += number * tileWidth
        return start
    }
}
