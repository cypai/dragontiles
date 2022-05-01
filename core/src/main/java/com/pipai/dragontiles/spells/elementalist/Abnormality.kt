package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatSubscribe
import com.pipai.dragontiles.combat.TileStatusChangeEvent
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.SimpleStatus
import com.pipai.dragontiles.utils.choose

class Abnormality : PowerSpell() {
    override val id: String = "base:spells:Abnormality"
    override val requirement: ComponentRequirement = Identical(4)
    override val rarity: Rarity = Rarity.RARE
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(6),
    )

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        api.addStatusToHero(AbnormalityStatus(5))
    }

    class AbnormalityStatus(amount: Int) : SimpleStatus("base:status:Abnormality", "gray.png", true, amount) {
        @CombatSubscribe
        suspend fun onTileStatus(ev: TileStatusChangeEvent, api: CombatApi) {
            if (ev.tileStatus != TileStatus.NONE) {
                ev.tiles.forEach {
                    if (it in api.combat.hand) {
                        val target = api.getLiveEnemies().choose(api.runData.seed.miscRng())
                        api.attack(target, Element.NONE, amount, listOf())
                    }
                }
            }
        }
    }
}
