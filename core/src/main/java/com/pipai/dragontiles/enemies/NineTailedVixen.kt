package com.pipai.dragontiles.enemies

import com.badlogic.gdx.math.MathUtils
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.status.Immortality
import com.pipai.dragontiles.status.Strength

class NineTailedVixen : Enemy() {

    override val id: String = "base:enemies:NineTailedVixen"
    override val assetName: String = "nine_tailed_fox.png"

    override val hpMax: Int = 90
    override val fluxMax: Int = 0

    private var turns: Int = 1

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Immortality(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        return when (turns % 3) {
            0 -> AttackIntent(this, 18, 1, Element.NONE)
            1 -> BuffIntent(this, listOf(Strength(3)), AttackIntent(this, 9, 1, Element.NONE))
            else -> {
                val fumbles: MutableList<Tile> = mutableListOf()
                repeat(9) {
                    fumbles.add(Tile.FumbleTile())
                }
                StrategicIntent(this, listOf(), listOf(), listOf(), {
                    api.addToOpenPool(api.createTiles(fumbles), Combatant.EnemyCombatant(this))
                })
            }
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
