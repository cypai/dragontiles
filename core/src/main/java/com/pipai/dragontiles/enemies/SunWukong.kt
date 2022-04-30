package com.pipai.dragontiles.enemies

import com.badlogic.gdx.math.Vector2
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.status.Minion
import com.pipai.dragontiles.utils.choose

class SunWukong : Enemy() {

    override val id: String = "base:enemies:SunWukong"
    override val assetName: String = "sun_wukong.png"

    override val hpMax: Int = 144
    override val fluxMax: Int = 0

    private var leftMinion: Enemy? = null
    private var rightMinion: Enemy? = null

    private val leftPosition = Vector2(0.5f, 4.5f)
    private val rightPosition = Vector2(5.5f, 4.5f)

    override suspend fun init(api: CombatApi) {
        summon(api)
        summon(api)
    }

    override fun getIntent(api: CombatApi): Intent {
        return if (leftMinion == null || rightMinion == null) {
            StrategicIntent(this, listOf(), listOf(), listOf(), {
                summon(api)
            })
        } else {
            AttackIntent(this, 12, 1, Element.NONE)
        }
    }

    private suspend fun summon(api: CombatApi) {
        val rng = api.runData.seed.miscRng()
        val availableMinions = listOf(Gnat(), Monkey(), Rat(), WolfPup())
        if (leftMinion == null) {
            val right = rightMinion
            val choice = if (right == null) {
                availableMinions.choose(rng)
            } else {
                availableMinions.filter { it::class != right::class }.choose(rng)
            }
            api.summonEnemy(choice, leftPosition)
            api.addStatusToEnemy(choice, Minion())
            leftMinion = choice
        } else if (rightMinion == null) {
            val left = leftMinion
            val choice = if (left == null) {
                availableMinions.choose(rng)
            } else {
                availableMinions.filter { it::class != left::class }.choose(rng)
            }
            api.summonEnemy(choice, rightPosition)
            api.addStatusToEnemy(choice, Minion())
            rightMinion = choice
        }
    }

    override fun nextIntent(api: CombatApi): Intent {
        return getIntent(api)
    }

    @CombatSubscribe
    fun onEnemyDefeat(ev: EnemyDefeatedEvent, api: CombatApi) {
        when (ev.enemy) {
            leftMinion -> leftMinion = null
            rightMinion -> rightMinion = null
        }
    }
}
