package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.*

class PingHu : Sorcery() {
    override val id = "base:spells:PingHu"
    override val rarity = Rarity.STARTER
    override val requirement = object : CustomRequirement() {
        override val type: SetType = SetType.SEQUENTIAL
        override var suitGroup: SuitGroup = SuitGroup.ELEMENTAL
        override val description: String = "All melds are sequences."
        override val showTooltip: Boolean = true

        override fun examples(): List<List<Tile>> {
            return listOf(listOf(
                Tile.ElementalTile(Suit.FIRE, 1),
                Tile.ElementalTile(Suit.FIRE, 2),
                Tile.ElementalTile(Suit.FIRE, 3),
                Tile.ElementalTile(Suit.ICE, 2),
                Tile.ElementalTile(Suit.ICE, 3),
                Tile.ElementalTile(Suit.ICE, 4),
                Tile.ElementalTile(Suit.LIGHTNING, 7),
                Tile.ElementalTile(Suit.LIGHTNING, 8),
                Tile.ElementalTile(Suit.LIGHTNING, 9),
            ))
        }

        override fun satisfied(slots: List<TileInstance>): Boolean {
            throw NotImplementedError()
        }

        override fun satisfied(fullCastHand: FullCastHand): Boolean {
            return fullCastHand.melds.all { it.type == MeldType.SEQUENCE }
        }
    }
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(20)
    )

    override fun flags(): List<CombatFlag> = listOf(CombatFlag.PIERCING)

    override val scoreable: Boolean = true

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.NONE, baseDamage(), flags())
    }
}
