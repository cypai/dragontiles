package com.badlogic.gdx.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Array
import com.esotericsoftware.spine.SkeletonData
import com.esotericsoftware.spine.SkeletonJson

class SkeletonDataLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<SkeletonData, SkeletonDataLoader.SkeletonDataParameter>(resolver) {

    var skeletonData: SkeletonData? = null

    override fun loadAsync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: SkeletonDataParameter?
    ) {
        val atlas = manager!!.get(parameter!!.atlas, TextureAtlas::class.java)
        val reader = SkeletonJson(atlas)
        skeletonData = reader.readSkeletonData(file)
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: SkeletonDataParameter?
    ): SkeletonData {
        loadAsync(manager, fileName, file, parameter)
        return skeletonData!!
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: SkeletonDataParameter?
    ): Array<AssetDescriptor<Any>> {
        val deps = Array<AssetDescriptor<TextureAtlas>>()
        deps.add(AssetDescriptor(parameter!!.atlas, TextureAtlas::class.java))
        return deps as Array<AssetDescriptor<Any>>
    }

    data class SkeletonDataParameter(val atlas: String) : AssetLoaderParameters<SkeletonData>() {
    }
}
