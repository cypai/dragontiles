package com.pipai.dragontiles.data

data class AssetConfig(val type: AssetType, val scaleX: Float, val scaleY: Float)

enum class AssetType {
    SPRITE, SPINE
}
