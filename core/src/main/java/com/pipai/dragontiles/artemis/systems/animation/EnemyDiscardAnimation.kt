package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class EnemyDiscardAnimation(
        private val enemyId: Int,
        private val tile: TileInstance,
        private val poolLocation: Int,
        layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var mAttackCircle: ComponentMapper<AttackCircleComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>

    override fun startAnimation() {
        val attackEntityId = world.fetch(allOf(AttackCircleComponent::class))
                .first { mAttackCircle.get(it).enemyId == enemyId }
        val cXy = mXy.get(attackEntityId)
        val entityId = createTile(tile, cXy.x, cXy.y)
        moveTile(entityId, layout.openTilePosition(poolLocation)) {
            endAnimation()
        }
    }

}
