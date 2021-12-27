package com.pipai.dragontiles.gui

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.GameConfig
import com.pipai.dragontiles.data.TileSkin

data class CombatUiLayout(val config: GameConfig, val tileSkin: TileSkin, val maxHandSize: Int) {

    val openPoolSize = 9

    val cardWidth = 140f
    val cardHeight = 196f

    val spellCastPosition = Vector2(config.resolution.width / 2f - cardWidth / 2f, config.resolution.height / 2f)

    fun spellStartPosition(number: Int) = Vector2(cardWidth * number, -cardHeight / 2f)

    val handCenter = Vector2(config.resolution.width / 2f, cardHeight)
    val handBlCorner = Vector2(handCenter.x - tileSkin.width * maxHandSize / 2f, handCenter.y - tileSkin.height / 2f)

    val openPoolCenter = Vector2(config.resolution.width / 2f, cardHeight + tileSkin.height * 2)
    val openBlCorner = Vector2(openPoolCenter.x - tileSkin.width * openPoolSize / 2f, openPoolCenter.y - tileSkin.height / 2f)

    fun openTilePosition(number: Int) = Vector2(openBlCorner.x + tileSkin.width * number, openBlCorner.y)
    fun handTilePosition(number: Int) = Vector2(handBlCorner.x + tileSkin.width * number, handBlCorner.y)
    fun handActiveTilePosition(number: Int) = Vector2(handBlCorner.x + tileSkin.width * number, handBlCorner.y + 16f)
    fun handRuneTilePosition(tilesInHand: Int, assignedSizes: List<Int>, runeSetIndex: Int, number: Int): Vector2 {
        val start = handTilePosition(tilesInHand)
        start.x += tileSkin.width / 2f
        val applicableSizeList = assignedSizes.subList(0, runeSetIndex)
        start.x += applicableSizeList.sum() * tileSkin.width + applicableSizeList.size * tileSkin.width / 2f
        start.x += number * tileSkin.width
        return start
    }

    val optionListTlPosition = Vector2(config.resolution.width / 4f, spellCastPosition.y + cardHeight)
}
