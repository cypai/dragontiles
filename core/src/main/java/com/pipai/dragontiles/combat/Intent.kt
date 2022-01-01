package com.pipai.dragontiles.combat

import com.pipai.dragontiles.artemis.systems.animation.DefaultAttackAnimation
import com.pipai.dragontiles.artemis.systems.animation.DelayAnimation
import com.pipai.dragontiles.artemis.systems.animation.SpineAnimation
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.chooseAmount
import kotlin.random.Random

interface Intent {
    val enemy: Enemy
    val displayData: Pair<IntentDisplayData, IntentDisplayData?>
    suspend fun execute(api: CombatApi)
}

sealed class IntentDisplayData {
    data class AttackIntentDisplay(val attackPower: Int, val multistrike: Int, val element: Element) :
        IntentDisplayData()

    data class VentIntentDisplay(val amount: Int) : IntentDisplayData()
    class BuffIntentDisplay : IntentDisplayData()
    class DebuffIntentDisplay : IntentDisplayData()
    class StunnedIntentDisplay : IntentDisplayData()
}

data class AttackIntent(
    override val enemy: Enemy,
    val attackPower: Int,
    val multistrike: Int,
    val element: Element,
    val animation: String? = null,
    val animationEndEvent: String? = null,
) : Intent {

    override val displayData = Pair(IntentDisplayData.AttackIntentDisplay(attackPower, multistrike, element), null)

    override suspend fun execute(api: CombatApi) {
        if (animation == null) {
            api.animate(DefaultAttackAnimation(enemy))
        } else {
            api.animate(SpineAnimation(enemy, animation, animationEndEvent))
        }
        repeat(multistrike) {
            api.attackHero(enemy, element, attackPower, listOf())
            api.animate(DelayAnimation(0.1f))
        }
        api.animate(DelayAnimation(0.9f))
    }
}

data class StunnedIntent(override val enemy: Enemy) : Intent {

    override val displayData = Pair(IntentDisplayData.StunnedIntentDisplay(), null)

    override suspend fun execute(api: CombatApi) {
    }
}

data class BuffIntent(
    override val enemy: Enemy, val status: Status, val attackIntent: AttackIntent?,
) : Intent {

    override val displayData = if (attackIntent == null) {
        Pair(IntentDisplayData.BuffIntentDisplay(), null)
    } else {
        Pair(attackIntent.displayData.first, IntentDisplayData.BuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        api.addStatusToEnemy(enemy, status)
    }
}

data class DebuffIntent(
    override val enemy: Enemy,
    val status: Status?,
    val attackIntent: AttackIntent?,
    val inflictTileStatuses: List<TileStatusInflictStrategy>
) : Intent {

    override val displayData = if (attackIntent == null) {
        Pair(IntentDisplayData.DebuffIntentDisplay(), null)
    } else {
        Pair(attackIntent.displayData.first, IntentDisplayData.DebuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        if (attackIntent == null) {
            api.animate(DefaultAttackAnimation(enemy))
        } else {
            attackIntent.execute(api)
        }
        status?.let { api.addStatusToHero(status) }
        inflictTileStatuses.forEach { strategy ->
            api.inflictTileStatusOnHand(strategy)
        }
    }
}

data class FumbleIntent(
    override val enemy: Enemy, val amount: Int, val intent: Intent?
) : Intent {

    override val displayData = if (intent == null) {
        Pair(IntentDisplayData.DebuffIntentDisplay(), null)
    } else {
        Pair(intent.displayData.first, IntentDisplayData.DebuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        intent?.execute(api)
        val tiles: MutableList<Tile> = mutableListOf()
        repeat(amount) {
            tiles.add(Tile.FumbleTile())
        }
        api.addTilesToHand(tiles, TileStatus.NONE)
    }
}

data class VentIntent(
    override val enemy: Enemy, val amount: Int, val status: Status?
) : Intent {

    override val displayData = if (status == null) {
        Pair(IntentDisplayData.VentIntentDisplay(amount), null)
    } else {
        Pair(IntentDisplayData.VentIntentDisplay(amount), IntentDisplayData.BuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        api.enemyLoseFlux(enemy, amount)
        status?.let { api.addStatusToEnemy(enemy, it) }
    }
}

interface TileStatusInflictStrategy {
    val tileStatus: TileStatus
    val amount: Int
    val notEnoughStrategy: NotEnoughStrategy
    fun select(hand: List<TileInstance>, rng: Random): List<TileInstance>

    enum class NotEnoughStrategy {
        SKIP, RANDOM,
    }
}

data class RandomTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int,
    override val notEnoughStrategy: TileStatusInflictStrategy.NotEnoughStrategy
) : TileStatusInflictStrategy {

    override fun select(hand: List<TileInstance>, rng: Random): List<TileInstance> {
        return hand
            .filter { it.tileStatus == TileStatus.NONE }
            .chooseAmount(amount, rng)
    }
}

data class TerminalTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int,
    override val notEnoughStrategy: TileStatusInflictStrategy.NotEnoughStrategy
) : TileStatusInflictStrategy {

    override fun select(hand: List<TileInstance>, rng: Random): List<TileInstance> {
        return hand
            .filter { t ->
                t.tileStatus == TileStatus.NONE
                        && t.tile !is Tile.FumbleTile
                        && terminal(t.tile, hand.map { it.tile })
            }
            .chooseAmount(amount, rng)
    }
}
