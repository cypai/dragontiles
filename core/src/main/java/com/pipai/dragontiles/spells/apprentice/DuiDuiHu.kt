package com.pipai.dragontiles.spells.apprentice

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.spells.*

class DuiDuiHu : Sorcery() {
    override val id = "base:spells:DuiDuiHu"
    override val rarity = Rarity.UNCOMMON
    override val requirement = object : CustomRequirement() {
        override val type: SetType = SetType.IDENTICAL
        override var suitGroup: SuitGroup = SuitGroup.ANY_NO_FUMBLE
        override val description: String = "All melds are identical."
        override val showTooltip: Boolean = true

        override fun examples(): List<List<Tile>> {
            return listOf(listOf(
                Tile.ElementalTile(Suit.FIRE, 1),
                Tile.ElementalTile(Suit.FIRE, 1),
                Tile.ElementalTile(Suit.FIRE, 1),
                Tile.ElementalTile(Suit.ICE, 3),
                Tile.ElementalTile(Suit.ICE, 3),
                Tile.ElementalTile(Suit.ICE, 3),
                Tile.StarTile(StarType.EARTH),
                Tile.StarTile(StarType.EARTH),
                Tile.StarTile(StarType.EARTH),
            ))
        }

        override fun satisfied(slots: List<TileInstance>): Boolean {
            throw NotImplementedError()
        }

        override fun satisfied(fullCastHand: FullCastHand): Boolean {
            return fullCastHand.melds.all { it.type == MeldType.IDENTICAL }
        }
    }
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        AttackDamageAspect(30)
    )

    override fun flags(): List<CombatFlag> = listOf(CombatFlag.PIERCING)

    override val scoreable: Boolean = true

    override suspend fun onCast(hand: FullCastHand, api: CombatApi) {
        api.aoeAttack(Element.NONE, baseDamage(), flags())
    }
}
