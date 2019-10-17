package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import net.mostlyoriginal.api.event.common.Event

data class TileClickEvent(val entityId: Int) : Event

data class AttackCircleHoverEnterEvent(val cAttackCircle: AttackCircleComponent) : Event
class AttackCircleHoverExitEvent : Event
