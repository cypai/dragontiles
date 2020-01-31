package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.components.AnchoredLineComponent
import com.pipai.dragontiles.artemis.components.MutualDestroyComponent
import com.pipai.dragontiles.artemis.systems.combat.TileIdSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.gui.CombatUiLayout
import com.pipai.dragontiles.gui.SpellCard

class AdjustHandAnimation(private val tileLocations: List<Pair<TileInstance, Int>>,
                          private val assigned: MutableMap<Int, List<TileInstance>>,
                          layout: CombatUiLayout) : TileAnimation(layout) {

    private lateinit var mLine: ComponentMapper<AnchoredLineComponent>
    private lateinit var mMutualDestroy: ComponentMapper<MutualDestroyComponent>

    private lateinit var sTileId: TileIdSystem
    private lateinit var sCombatUi: CombatUiSystem

    override fun startAnimation() {
        val activeTiles = sCombatUi.activeTiles()
        tileLocations.forEach { (tile, index) ->
            val entityId = sTileId.getEntityId(tile.id)
            mLine.remove(entityId)
            if (tile in activeTiles) {
                moveTile(entityId, layout.handActiveTilePosition(index), 0.1f) {
                    endAnimation()
                }
            } else {
                moveTile(entityId, layout.handTilePosition(index), 0.3f) {
                    endAnimation()
                }
            }
        }
        val assignedSizes = assigned.toList()
                .sortedBy { it.first }
                .map { it.second.size }
        var runeSetNumber = 0
        assigned.entries.sortedBy { it.key }.forEach {
            val spellIndex = it.key
            val assignedTiles = it.value
            assignedTiles.forEachIndexed { index, tile ->
                val entityId = sTileId.getEntityId(tile.id)
                val cLine = mLine.create(entityId)
                cLine.color = Color.GRAY
                val spellCardId = sCombatUi.spellCardEntityId(spellIndex)!!
                cLine.safeSetAnchor1(entityId, spellCardId, mMutualDestroy)
                cLine.anchor1Offset.set(SpellCard.cardWidth / 2f, SpellCard.cardHeight)
                cLine.anchor2 = entityId
                moveTile(entityId, layout.handRuneTilePosition(tileLocations.size, assignedSizes, runeSetNumber, index), 0.3f) {
                    endAnimation()
                }
            }
            runeSetNumber++
        }
    }

}
