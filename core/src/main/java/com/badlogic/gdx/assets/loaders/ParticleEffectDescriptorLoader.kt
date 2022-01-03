package com.badlogic.gdx.assets.loaders

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Array
import com.talosvfx.talos.runtime.ParticleEffectDescriptor

class ParticleEffectDescriptorLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<ParticleEffectDescriptor, ParticleEffectDescriptorLoader.ParticleEffectDescriptorParameter>(
        resolver
    ) {

    var descriptor: ParticleEffectDescriptor? = null

    override fun loadAsync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: ParticleEffectDescriptorParameter?
    ) {
        val atlas = manager!!.get(parameter!!.atlas, TextureAtlas::class.java)
        descriptor = ParticleEffectDescriptor(file, atlas)
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: ParticleEffectDescriptorParameter?
    ): ParticleEffectDescriptor {
        loadAsync(manager, fileName, file, parameter)
        return descriptor!!
    }

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: ParticleEffectDescriptorParameter?
    ): Array<AssetDescriptor<Any>> {
        val deps = Array<AssetDescriptor<TextureAtlas>>()
        deps.add(AssetDescriptor(parameter!!.atlas, TextureAtlas::class.java))
        return deps as Array<AssetDescriptor<Any>>
    }

    data class ParticleEffectDescriptorParameter(val atlas: String) :
        AssetLoaderParameters<ParticleEffectDescriptor>() {
    }
}
