package com.pipai.dragontiles.artemis.systems.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem

class InputProcessingSystem : NoProcessingSystem() {

    private val multiplexer: InputMultiplexer = InputMultiplexer()
    private val inactiveMultiplexer: InputMultiplexer = InputMultiplexer()

    override fun initialize() {
        Gdx.input.inputProcessor = multiplexer
    }

    fun addProcessor(processor: InputProcessor) {
        multiplexer.addProcessor(processor)
    }

    fun addAlwaysOnProcessor(processor: InputProcessor) {
        multiplexer.addProcessor(processor)
        inactiveMultiplexer.addProcessor(processor)
    }

    fun activateInput() {
        Gdx.input.inputProcessor = multiplexer
    }

    fun deactivateInput() {
        Gdx.input.inputProcessor = inactiveMultiplexer
    }
}
