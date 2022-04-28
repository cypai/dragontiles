package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.HandLocationComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class TilesAddedToHandAnimation(
    private val tiles: List<Pair<TileInstance, Int>>,
    private val originator: Enemy?,
    layout: CombatUiLayout
) : TileAnimation(layout) {

    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mHand: ComponentMapper<HandLocationComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>

    override fun startAnimation() {
        tiles.forEach { (tile, handIndex) ->
            val position = if (originator == null) {
                layout.handTilePosition(handIndex)
            } else {
                val entityId = world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == originator }
                mXy.get(entityId).toVector2()
            }
            val entityId = createTile(tile, position.x, position.y)
            val cHand = mHand.create(entityId)
            cHand.setByLocation(handIndex)
        }
        endAnimation()
    }

}
