package com.pipai.dragontiles.data

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TileSkin(texture: Texture) {

    private val regions: MutableMap<Tile, TextureRegion> = mutableMapOf()

    init {
        for (x in 1..9) {
            regions[Tile.ElementalTile(Suit.FIRE, x)] = TextureRegion(texture, 32 * (x - 1), 0, 32, 48)
            regions[Tile.ElementalTile(Suit.ICE, x)] = TextureRegion(texture, 32 * (x - 1), 48, 32, 48)
            regions[Tile.ElementalTile(Suit.LIGHTNING, x)] = TextureRegion(texture, 32 * (x - 1), 96, 32, 48)
        }
        regions[Tile.LifeTile(LifeType.LIFE)] = TextureRegion(texture, 0, 144, 32, 48)
        regions[Tile.LifeTile(LifeType.MIND)] = TextureRegion(texture, 32, 144, 32, 48)
        regions[Tile.LifeTile(LifeType.SOUL)] = TextureRegion(texture, 64, 144, 32, 48)
        regions[Tile.StarTile(StarType.EARTH)] = TextureRegion(texture, 0, 192, 32, 48)
        regions[Tile.StarTile(StarType.MOON)] = TextureRegion(texture, 32, 192, 32, 48)
        regions[Tile.StarTile(StarType.SUN)] = TextureRegion(texture, 64, 192, 32, 48)
        regions[Tile.StarTile(StarType.STAR)] = TextureRegion(texture, 96, 192, 32, 48)
    }

    fun regionFor(tile: Tile) = regions[tile]!!

}
