package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.ComponentMapper
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.combat.Combatant
import com.pipai.dragontiles.data.StringLocalized
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch

class TalkAnimation(val combatant: Combatant, val localized: StringLocalized) : Animation() {
    private lateinit var mEnemy: ComponentMapper<EnemyComponent>
    private lateinit var mXy: ComponentMapper<XYComponent>
    private lateinit var mText: ComponentMapper<TextComponent>
    private lateinit var mAlphaInterp: ComponentMapper<AlphaInterpolationComponent>

    override fun startAnimation() {
        // Currently a carbon copy of TextAnimation, but will change someday
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
        cText.text = game.gameStrings.textLocalization(localized)
        cText.size = TextSize.NORMAL
        cText.color = Color.BLACK.cpy()

        val cAlphaInterp = mAlphaInterp.create(textEntityId)
        cAlphaInterp.set(1f, 0f, 2f, Interpolation.pow2In, EndStrategy.DESTROY)

        endAnimation()
    }
}
