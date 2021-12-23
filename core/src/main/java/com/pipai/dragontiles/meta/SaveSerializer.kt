package com.pipai.dragontiles.meta

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class SaveSerializer {

    private val mapper = jacksonObjectMapper()

    fun serialize(save: Save): String {
        return mapper.writeValueAsString(save)
    }

    fun deserialize(string: String): Save {
        return mapper.readValue(string)
    }
}
