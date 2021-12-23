package com.pipai.dragontiles.meta

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class SaveSerializer {

    private val module = SerializersModule {
    }

    private val format = Json { serializersModule = module }

    fun serialize(save: Save): String {
        return format.encodeToString(save)
    }

    fun deserialize(string: String): Save {
        return format.decodeFromString(string)
    }
}
