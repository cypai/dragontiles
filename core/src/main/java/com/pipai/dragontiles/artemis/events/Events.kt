package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.data.PricedSpell
import com.pipai.dragontiles.spells.Spell
import net.mostlyoriginal.api.event.common.Event

data class EnemyClickEvent(val entityId: Int, val button: Int) : Event
data class TileClickEvent(val entityId: Int, val button: Int) : Event

data class EnemyHoverEnterEvent(val cEnemy: EnemyComponent) : Event
class EnemyHoverExitEvent : Event

data class PricedSpellClickEvent(val entityId: Int, val pricedSpell: PricedSpell) : Event

data class MapNodeClickEvent(val floorNum: Int, val index: Int) : Event
class ShopClickEvent : Event

data class ReplaceSpellQueryEvent(val spell: Spell) : Event
class TopRowUiUpdateEvent : Event
data class GoldChangeEvent(val amount: Int) : Event
