package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.data.PricedItem
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.upgrades.SpellUpgrade
import net.mostlyoriginal.api.event.common.Event

data class EnemyClickEvent(val entityId: Int, val button: Int) : Event
data class TileClickEvent(val entityId: Int, val button: Int) : Event

data class EnemyHoverEnterEvent(val cEnemy: EnemyComponent) : Event
class EnemyHoverExitEvent : Event

data class PricedItemClickEvent(val entityId: Int, val pricedItem: PricedItem) : Event

data class MapNodeClickEvent(val floorNum: Int, val index: Int) : Event
data class MapDisplayUiEvent(val isNowShowing: Boolean) : Event
data class DeckDisplayUiEvent(val isNowShowing: Boolean) : Event

data class SpellGainedEvent(val spell: Spell) : Event
data class ReplaceSpellQueryEvent(val spell: Spell) : Event
data class UpgradeSpellQueryEvent(
    val upgrade: SpellUpgrade,
    val type: DeckQueryType,
    val upgradeCallback: () -> Unit,
    val skipCallback: () -> Unit
) : Event

enum class DeckQueryType {
    SKIPPABLE, CANCELABLE
}

class TransformSpellQueryEvent : Event
class TopRowUiUpdateEvent : Event
data class PotionUseUiEvent(val potionSlotIndex: Int) : Event
