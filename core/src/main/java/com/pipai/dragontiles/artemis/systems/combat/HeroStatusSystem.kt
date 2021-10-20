package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.BackendId
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import com.pipai.dragontiles.utils.require

class HeroStatusSystem() : IteratingSystem(allOf()) {
    private val mHero by require<HeroComponent>()
    private val mTextLabel by require<TextLabelComponent>()

    override fun process(entityId: EntityId) {
        val cHero = mHero.get(entityId)
        val cTextLabel = mTextLabel.get(entityId)
        cTextLabel.text = "${cHero.hp}/${cHero.hpMax}   ${cHero.flux}/${cHero.fluxMax}"
    }
}
