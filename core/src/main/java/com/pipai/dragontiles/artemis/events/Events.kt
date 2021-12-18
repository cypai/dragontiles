package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.spells.Spell
import net.mostlyoriginal.api.event.common.Event

data class EnemyClickEvent(val entityId: Int, val button: Int) : Event
data class TileClickEvent(val entityId: Int, val button: Int) : Event

data class EnemyHoverEnterEvent(val cEnemy: EnemyComponent) : Event
class EnemyHoverExitEvent : Event

data class SpellCardClickEvent(val entityId: Int, val spell: Spell, val origin: Origin) : Event {
    enum class Origin {
        COMBAT, REWARD, DECK_DISPLAY, SHOP
    }
}

data class MapNodeClickEvent(val floorNum: Int, val index: Int) : Event
class ShopClickEvent : Event
