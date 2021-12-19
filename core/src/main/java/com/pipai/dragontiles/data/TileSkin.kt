package com.pipai.dragontiles.data

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TileSkin(texture: Texture) {

    private val elementalRegions: MutableMap<Pair<Suit, Int>, TextureRegion> = mutableMapOf()
    private val lifeRegions: MutableMap<LifeType, TextureRegion> = mutableMapOf()
    private val starRegions: MutableMap<StarType, TextureRegion> = mutableMapOf()
    private val fumbleRegion = TextureRegion(texture, 96, 144, 32, 48)

    val width = 32
    val height = 48

    init {
        for (x in 1..9) {
            elementalRegions[Pair(Suit.FIRE, x)] = TextureRegion(texture, 32 * (x - 1), 0, 32, 48)
            elementalRegions[Pair(Suit.ICE, x)] = TextureRegion(texture, 32 * (x - 1), 48, 32, 48)
            elementalRegions[Pair(Suit.LIGHTNING, x)] = TextureRegion(texture, 32 * (x - 1), 96, 32, 48)
        }
        lifeRegions[LifeType.LIFE] = TextureRegion(texture, 0, 144, 32, 48)
        lifeRegions[LifeType.MIND] = TextureRegion(texture, 32, 144, 32, 48)
        lifeRegions[LifeType.SOUL] = TextureRegion(texture, 64, 144, 32, 48)
        starRegions[StarType.EARTH] = TextureRegion(texture, 0, 192, 32, 48)
        starRegions[StarType.MOON] = TextureRegion(texture, 32, 192, 32, 48)
        starRegions[StarType.SUN] = TextureRegion(texture, 64, 192, 32, 48)
        starRegions[StarType.STAR] = TextureRegion(texture, 96, 192, 32, 48)
    }

    fun regionFor(tile: Tile) = when (tile) {
        is Tile.ElementalTile -> elementalRegions[Pair(tile.suit, tile.number)]
        is Tile.LifeTile -> lifeRegions[tile.type]
        is Tile.StarTile -> starRegions[tile.type]
        is Tile.FumbleTile -> fumbleRegion
    }

}
