package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.data.Tile
import net.mostlyoriginal.api.event.common.Event

data class TileClickEvent(val entityId: Int) : Event

data class TileOptionClickEvent(val tile: Tile) : Event
