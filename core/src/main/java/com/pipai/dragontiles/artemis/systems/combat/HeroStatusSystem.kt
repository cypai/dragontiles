package com.pipai.dragontiles.artemis.systems.combat

import com.artemis.systems.IteratingSystem
import com.pipai.dragontiles.artemis.EntityId
import com.pipai.dragontiles.artemis.components.HeroComponent
import com.pipai.dragontiles.artemis.components.TextComponent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.require

class HeroStatusSystem() : IteratingSystem(allOf()) {
    private val mHero by require<HeroComponent>()
    private val mText by require<TextComponent>()

    override fun process(entityId: EntityId) {
        val cHero = mHero.get(entityId)
        val cText = mText.get(entityId)
        cText.text = "${cHero.hp}/${cHero.hpMax}   ${cHero.flux}/${cHero.fluxMax}"
    }
}
