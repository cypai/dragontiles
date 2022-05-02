package com.pipai.dragontiles.enemies

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.status.Immortality
import com.pipai.dragontiles.status.Minion
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.status.Strength

class ChangE : Enemy() {

    override val id: String = "base:enemies:ChangE"
    override val assetName: String = "chang_e.png"

    override val hpMax: Int = 90
    override val fluxMax: Int = 80

    private var turns: Int = 0

    override suspend fun init(api: CombatApi) {
        api.addStatusToEnemy(this, Immortality(1))
    }

    override fun getIntent(api: CombatApi): Intent {
        if (api.getLiveEnemies().none { api.enemyHasStatus(it, Minion::class) }) {
            return StrategicIntent(this, listOf(), listOf(), listOf(), {
                turns = 0
                api.summonEnemy(MoonRabbit(), Vector2(4f, 4.5f))
            })
        }
        if (api.getLiveEnemies().any { it is Yumi3 }) {
            val originalIntent = when (turns % 3) {
                0 -> DebuffIntent(
                    this,
                    listOf(),
                    listOf(
                        NonorphanedTileStatusInflictStrategy(
                            TileStatus.VOLATILE,
                            4,
                        )
                    ),
                    null
                )
                1 -> DebuffIntent(
                    this,
                    listOf(),
                    listOf(
                        NonorphanedTileStatusInflictStrategy(
                            TileStatus.VOLATILE,
                            5,
                        )
                    ),
                    null
                )
                else -> BuffIntent(
                    this,
                    listOf(),
                    AttackIntent(this, 9, 1, Element.NONE),
                    {
                        api.addAoeStatus(Strength(3))
                    }
                )
            }
            return if (api.heroHasStatus(Overloaded::class)) {
                AttackIntent(this, 20, 1, Element.NONE)
            } else {
                originalIntent
            }
        } else {
            return AttackIntent(this, 30, 1, Element.NONE)
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }
}
