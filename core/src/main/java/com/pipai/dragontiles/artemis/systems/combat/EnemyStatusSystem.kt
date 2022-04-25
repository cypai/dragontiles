package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.EnemyComponent
import com.pipai.dragontiles.artemis.components.TextComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class EnemyStatusSystem(private val game: DragonTilesGame) : IteratingSystem(allOf()) {
    private val mEnemy by require<EnemyComponent>()
    private val mText by require<TextComponent>()

    override fun process(entityId: EntityId) {
        val cEnemy = mEnemy.get(entityId)
        val cText = mText.get(entityId)
        cText.text = "${game.gameStrings.nameDescLocalization(cEnemy.strId).name}    ${cEnemy.hp}/${cEnemy.hpMax}   ${cEnemy.flux}/${cEnemy.fluxMax}"
    }
}
