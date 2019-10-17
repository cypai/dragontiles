package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.pipai.dragontiles.artemis.components.AttackCircleComponent
import com.pipai.dragontiles.artemis.events.AttackCircleHoverEnterEvent
import com.pipai.dragontiles.artemis.events.AttackCircleHoverExitEvent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.data.GameStrings
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.Subscribe

class AttackCircleHoverSystem(private val gameStrings: GameStrings) : NoProcessingSystem() {

    private lateinit var stage: Stage

    private val sTooltip by system<TooltipSystem>()
    private val sQueryUiSystem by system<CombatQueryUiSystem>()

    override fun initialize() {
        stage = sQueryUiSystem.stage
    }

    @Subscribe
    fun handleEnter(ev: AttackCircleHoverEnterEvent) {
        val ac = ev.cAttackCircle
        val l = gameStrings.nameDescLocalization(ac.strId)
        sTooltip.addText("Spell: ${l.name}", l.description, false)
        sTooltip.addText("Current Power", spellPowerText(ac), false)
        sTooltip.addKeyword("@AttackPower")
        sTooltip.addKeyword("@EffectPower")
        sTooltip.addKeywordsInString(l.description)
        sTooltip.showTooltip(stage)
    }

    private fun spellPowerText(ac: AttackCircleComponent): String {
        return when {
            ac.effectPower == 0 -> {
                "%s - %s = %s\nElement: %s".format(
                        ac.attackPower,
                        ac.counteredAttackPower,
                        ac.attackPower - ac.counteredAttackPower,
                        ac.element
                )
            }
            ac.attackPower == 0 -> {
                "(%s) - (%s) = (%s)\nElement: %s".format(
                        ac.effectPower,
                        ac.counteredEffectPower,
                        ac.effectPower - ac.counteredEffectPower,
                        ac.element
                )
            }
            else -> {
                "%s + (%s) - %s - (%s) = %s + (%s)\nElement: %s".format(
                        ac.attackPower,
                        ac.effectPower,
                        ac.counteredAttackPower,
                        ac.counteredEffectPower,
                        ac.attackPower - ac.counteredAttackPower,
                        ac.effectPower - ac.counteredEffectPower,
                        ac.element
                )
            }
        }
    }

    @Subscribe
    fun handleExit(ev: AttackCircleHoverExitEvent) {
        sTooltip.hideTooltip()
    }

}
