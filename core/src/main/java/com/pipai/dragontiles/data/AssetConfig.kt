package com.pipai.dragontiles.data

data class AssetConfig(val type: AssetType, val width: Float)

enum class AssetType {
    SPRITE, SPINE
}
