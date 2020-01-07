package com.pipai.dragontiles.artemis.events

import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.enemies.Enemy
import net.mostlyoriginal.api.event.common.Event

data class EnemyClickEvent(val entityId: Int, val button: Int) : Event
data class AttackCircleClickEvent(val entityId: Int, val button: Int) : Event
data class TileClickEvent(val entityId: Int, val button: Int) : Event

data class EnemyHoverEnterEvent(val cEnemy: EnemyComponent) : Event
class EnemyHoverExitEvent : Event
data class AttackCircleHoverEnterEvent(val cAttackCircle: AttackCircleComponent) : Event
class AttackCircleHoverExitEvent : Event
