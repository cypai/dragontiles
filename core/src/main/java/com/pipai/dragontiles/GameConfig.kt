package com.pipai.dragontiles

import com.badlogic.gdx.files.FileHandle
import com.pipai.dragontiles.utils.valueOfOrDefault
import java.util.*

enum class ScreenResolution(val width: Int, val height: Int, val description: String?) {
    // 4:3 Resolutions
    RES_1280_960(1280, 960, null),

    // 16:10 Resolutions
    RES_1280_800(1280, 800, null),
    RES_1680_1050(1680, 1050, null),

    // ~16:9 Resolutions
    RES_1366_768(1366, 768, "HD"),

    // 16:9 Resolutions
    RES_1280_720(1280, 720, "720p"),
    RES_1600_900(1600, 900, "900p"),
    RES_1920_1080(1920, 1080, "1080p");

    val aspectRatio = width.toFloat() / height.toFloat()

    fun toDebugString(): String {
        return "ScreenResolution[${width}x$height, $description]"
    }
}

private val DEFAULT_RESOLUTION = ScreenResolution.RES_1280_720

class GameConfig(private val configFile: FileHandle) {

    var resolution: ScreenResolution

    init {
        if (configFile.exists()) {
            val properties = Properties()
            configFile.reader().use { properties.load(it) }

            resolution = valueOfOrDefault(properties["resolution"] as String, DEFAULT_RESOLUTION)
        } else {
            resolution = DEFAULT_RESOLUTION
        }
    }

    fun writeToFile() {
        val properties = Properties()
        properties.put("resolution", resolution.toString())

        configFile.writer(false, "UTF-8").use { properties.store(it, "ADV Config") }
    }
}
