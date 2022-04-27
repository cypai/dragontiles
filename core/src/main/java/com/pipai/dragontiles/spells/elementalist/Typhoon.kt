package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TurnEndEvent
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.utils.findAs

class Typhoon : StandardSpell() {
    override val id: String = "base:spells:Typhoon"
    override val requirement: ComponentRequirement = Identical(3, SuitGroup.ANY_NO_FUMBLE)
    override val type: SpellType = SpellType.ATTACK
    override val targetType: TargetType = TargetType.AOE
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(5),
        FluxGainAspect(8),
        CountdownAspect(18, CountdownType.SCORE, this::cdCallback)
    )

    private var n = 1
    private var played = false

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        repeat(n) {
            api.aoeAttack(elemental(components()), baseDamage(), flags())
        }
    }

    override fun dynamicCustom(): Int {
        return n
    }

    @CombatSubscribe
    suspend fun onTurnEnd(ev: TurnEndEvent, api: CombatApi) {
        if (!played) {
            n = 1
            aspects.findAs(CountdownAspect::class)!!.current = 18
        }
        played = false
    }

    private suspend fun cdCallback(api: CombatApi) {
        api.score()
    }
}
