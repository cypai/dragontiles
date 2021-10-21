package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.TextLabelComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class EnemyStatusSystem(private val game: DragonTilesGame) : IteratingSystem(allOf()) {
    private val mEnemy by require<EnemyComponent>()
    private val mTextLabel by require<TextLabelComponent>()

    override fun process(entityId: EntityId) {
        val cEnemy = mEnemy.get(entityId)
        val cTextLabel = mTextLabel.get(entityId)
        cTextLabel.text = "${game.gameStrings.nameDescLocalization(cEnemy.strId).name}    ${cEnemy.hp}/${cEnemy.hpMax}   ${cEnemy.flux}/${cEnemy.fluxMax}"
    }
}
