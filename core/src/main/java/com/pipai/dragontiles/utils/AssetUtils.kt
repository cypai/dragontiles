package com.pipai.dragontiles.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound

fun enemyAssetPath(filename: String) = "assets/binassets/graphics/enemies/$filename"
fun spineAssetPath(name: String) = "assets/binassets/spine/$name/$name.json"
fun relicAssetPath(filename: String) = "assets/binassets/graphics/relics/$filename"
fun statusAssetPath(filename: String) = "assets/binassets/graphics/status/$filename"
fun upgradeAssetPath(filename: String) = "assets/binassets/graphics/upgrades/$filename"
fun potionAssetPath(filename: String) = "assets/binassets/graphics/potions/$filename"
fun intentAssetPath(filename: String) = "assets/binassets/graphics/intents/$filename"
fun particleAssetPath(filename: String) = "assets/binassets/particles/$filename"

fun AssetManager.sound(filename: String): Sound {
    return get("assets/binassets/audio/fx/$filename")
}
