package com.pipai.dragontiles.enemies

import com.pipai.dragontiles.artemis.systems.animation.TalkAnimation
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.data.AssetConfig
import com.pipai.dragontiles.data.AssetType
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.StringLocalized
import com.pipai.dragontiles.status.Strength

class ZhuBajie : Enemy() {

    override val id: String = "base:enemies:ZhuBajie"
    override val assetName: String = "zhu_bajie.png"
    override val assetConfig: AssetConfig = AssetConfig(AssetType.SPRITE, 2.5f)

    override val hpMax: Int = 160
    override val fluxMax: Int = 0

    private var turns: Int = 0

    override fun getIntent(api: CombatApi): Intent {
        if (turns == 0) {
            return DoNothingIntent(this, DoNothingType.SLEEPING, {
                api.animate(TalkAnimation(Combatant.EnemyCombatant(this), StringLocalized("base:text:ZhuBajieAwake")))
            })
        }
        val originalIntent = when (turns % 3) {
            1 -> AttackIntent(this, 1, 9, Element.NONE)
            2 -> StrategicIntent(this, listOf(Strength(1)), listOf(), listOf(), {
                val cornucopiaHp = api.getLiveEnemies()
                    .filterIsInstance<Cornucopia>()
                    .sumOf { it.hp }    // Update the amount
                if (cornucopiaHp > 0) {
                    api.animate(
                        TalkAnimation(
                            Combatant.EnemyCombatant(this),
                            StringLocalized("base:text:ZhuBajieOmNomNom")
                        )
                    )
                    api.healEnemy(this, cornucopiaHp)
                }
            })
            else -> AttackIntent(this, 1, 9, Element.NONE)
        }
        return originalIntent
    }

    override fun nextIntent(api: CombatApi): Intent {
        turns++
        return getIntent(api)
    }

    @CombatSubscribe
    suspend fun onEnemyDefeat(ev: EnemyDefeatedEvent, api: CombatApi) {
        if (ev.enemy != this) {
            api.animate(
                TalkAnimation(
                    Combatant.EnemyCombatant(this),
                    StringLocalized("base:text:ZhuBajieHowDareYou")
                )
            )
        }
    }
}
