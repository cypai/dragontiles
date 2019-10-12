package com.pipai.dragontiles.combat

import com.pipai.dragontiles.enemies.FlameTurtle
import com.pipai.dragontiles.hero.Hero
import com.pipai.dragontiles.spells.CastParams
import com.pipai.dragontiles.spells.Invoke
import com.pipai.dragontiles.spells.RampStrike
import net.mostlyoriginal.api.event.common.EventSystem
import org.junit.Assert
import org.junit.Test
import java.util.*

class RampStrikeTest {
    @Test
    fun testCombat() {
        val flameTurtle = FlameTurtle()
        val combat = Combat(Random(),
                Hero("Elementalist", 80, 80, 15, mutableListOf(Invoke(false), RampStrike(false))),
                mutableListOf(flameTurtle))

        val se = EventSystem()
        val controller = CombatController(combat, se)
        controller.initCombat()
        controller.runTurn()

        val invoke = controller.api.spellInstances[0]
        val rampStrike = controller.api.spellInstances[1]
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.spell.requirement.find(combat.hand).first())
        invoke.cast(CastParams(listOf(flameTurtle)), controller.api)
        Assert.assertEquals(5, rampStrike.baseDamage())

        controller.endTurn()
        Assert.assertEquals(3, rampStrike.baseDamage())
        invoke.fill(invoke.spell.requirement.find(combat.hand).first())
        invoke.cast(CastParams(listOf(flameTurtle)), controller.api)
        Assert.assertEquals(5, rampStrike.baseDamage())
    }
}
