package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.Localized
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.scry

class NameTextAnimation(val combatant: Combatant, val localized: Localized) : Animation() {
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mText: ComponentMapper<TextComponent>
    private lateinit var mAlphaInterp: ComponentMapper<AlphaInterpolationComponent>

    override fun startAnimation() {
        val entityId = when (combatant) {
            is Combatant.HeroCombatant -> {
                world.scry(allOf(HeroComponent::class)).first()
            }
            is Combatant.EnemyCombatant -> {
                world.scry(allOf(EnemyComponent::class)).first { mEnemy.get(it).enemy == combatant.enemy }
            }
        }

        val cCombatantXy = mXy.get(entityId)

        val textEntityId = world.create()
        mXy.create(textEntityId).setXy(cCombatantXy.x, cCombatantXy.y)

        val cText = mText.create(textEntityId)
        cText.text = game.gameStrings.nameLocalization(localized)
        cText.size = TextSize.LARGE
        cText.color = Color.BLACK.cpy()

        val cAlphaInterp = mAlphaInterp.create(textEntityId)
        cAlphaInterp.set(1f, 0f, 0.5f, Interpolation.pow2In, EndStrategy.DESTROY)

        endAnimation()
    }
}
