package com.pipai.dragontiles.artemis.screens

import com.artemis.ComponentMapper
import com.artemis.World
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.pipai.dragontiles.artemis.Tags
import com.pipai.dragontiles.artemis.components.OrthographicCameraComponent

@Wire
class StandardScreenInit(private val world: World) {

    private lateinit var mCamera: ComponentMapper<OrthographicCameraComponent>

    private lateinit var sTags: TagManager

    init {
        world.inject(this)
    }

    fun initialize() {
        val cameraId = world.create()
        mCamera.create(cameraId)
        sTags.register(Tags.CAMERA.toString(), cameraId)
    }

}
