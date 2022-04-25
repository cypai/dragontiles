package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class TextAnimation(val combatant: Combatant, val localized: Localized) : Animation() {
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mText: ComponentMapper<TextComponent>
    private lateinit var mTimer: ComponentMapper<TimerComponent>

    override fun startAnimation() {
        val entityId = when (combatant) {
            is Combatant.HeroCombatant -> {
                world.fetch(allOf(HeroComponent::class)).first()
            }
            is Combatant.EnemyCombatant -> {
                world.fetch(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == combatant.enemy }
            }
        }

        val cCombatantXy = mXy.get(entityId)

        val textEntityId = world.create()
        mXy.create(textEntityId).setXy(cCombatantXy.x, cCombatantXy.y)

        val cText = mText.create(textEntityId)
        cText.text = game.gameStrings.nameLocalization(localized).name
        cText.size = TextSize.NORMAL
        cText.color = Color.BLACK

        val cTimer = mTimer.create(textEntityId)
        cTimer.maxT = 1f
        cTimer.onEnd = EndStrategy.DESTROY
        endAnimation()
    }
}
