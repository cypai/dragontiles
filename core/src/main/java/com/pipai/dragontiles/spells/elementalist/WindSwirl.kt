package com.pipai.dragontiles.spells.elementalist

import com.pipai.dragontiles.combat.CombatApi
import com.pipai.dragontiles.combat.CombatFlag
import com.pipai.dragontiles.combat.RandomTileStatusInflictStrategy
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.spells.*
import com.pipai.dragontiles.status.Cryo
import com.pipai.dragontiles.status.Cryoshock
import com.pipai.dragontiles.status.Electro
import com.pipai.dragontiles.status.Pyro
import com.pipai.dragontiles.utils.getStackableCopy

class WindSwirl : StandardSpell() {
    override val id: String = "base:spells:WindSwirl"
    override val requirement: ComponentRequirement = AnyCombo(2, SuitGroup.LIFE)
    override val type: SpellType = SpellType.EFFECT
    override val targetType: TargetType = TargetType.SINGLE
    override val rarity: Rarity = Rarity.UNCOMMON
    override val aspects: MutableList<SpellAspect> = mutableListOf(
        FluxGainAspect(3),
    )

    override fun additionalKeywords(): List<String> =
        listOf("@Reactant", "@Reaction", "@Melt", "@Pyroblast", "@Cryoshock")

    override suspend fun onCast(params: CastParams, api: CombatApi) {
        val target = api.getEnemy(params.targets.first())
        api.getEnemies().forEach { enemy ->
            if (enemy.enemyId != target.enemyId) {
                val pyroAmount = api.enemyStatusAmount(target, Pyro::class)
                if (pyroAmount > 0) {
                    api.addStatusToEnemy(enemy, Pyro(pyroAmount))
                }
                val cryoAmount = api.enemyStatusAmount(target, Cryo::class)
                if (cryoAmount > 0) {
                    api.addStatusToEnemy(enemy, Cryo(cryoAmount))
                }
                val electroAmount = api.enemyStatusAmount(target, Electro::class)
                if (electroAmount > 0) {
                    api.addStatusToEnemy(enemy, Electro(electroAmount))
                }
                val cryoshockAmount = api.enemyStatusAmount(target, Cryoshock::class)
                if (cryoshockAmount > 0) {
                    api.addStatusToEnemy(enemy, Cryoshock(cryoshockAmount))
                }
            }
        }
    }
}
