package com.pipai.dragontiles.combat

import com.pipai.dragontiles.artemis.systems.animation.DelayAnimation
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.chooseAmount
import kotlin.random.Random

interface Intent {
    val enemy: Enemy
    val displayData: Pair<IntentDisplayData, IntentDisplayData?>
    val animation: IntentAnimation?
    suspend fun execute(api: CombatApi)
    fun flags(): List<CombatFlag> = listOf()
}

data class IntentAnimation(val animation: String, val endEvent: String)

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
    val callback: suspend (CombatApi) -> Unit = {},
    override val animation: IntentAnimation? = null
) : Intent {

    override val displayData = Pair(IntentDisplayData.AttackIntentDisplay(attackPower, multistrike, element), null)

    override fun flags(): List<CombatFlag> = listOf(CombatFlag.ATTACK)

    override suspend fun execute(api: CombatApi) {
        repeat(multistrike) {
            api.attackHero(enemy, element, attackPower, flags())
            api.animate(DelayAnimation(0.1f))
        }
        api.animate(DelayAnimation(0.9f))
    }
}

data class DoNothingIntent(
    override val enemy: Enemy,
    val type: DoNothingType,
    val callback: suspend (CombatApi) -> Unit = {},
    override val animation: IntentAnimation? = null
) : Intent {

    override val displayData = Pair(IntentDisplayData.StunnedIntentDisplay(), null)

    override suspend fun execute(api: CombatApi) {
    }
}

enum class DoNothingType {
    STUNNED, SLEEPING, WAITING
}

data class BuffIntent(
    override val enemy: Enemy,
    val buffs: List<Status>,
    val attackIntent: AttackIntent?,
    val callback: suspend (CombatApi) -> Unit = {},
    override val animation: IntentAnimation? = null,
) : Intent {

    override val displayData = if (attackIntent == null) {
        Pair(IntentDisplayData.BuffIntentDisplay(), null)
    } else {
        Pair(attackIntent.displayData.first, IntentDisplayData.BuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        buffs.forEach { api.addStatusToEnemy(enemy, it) }
        callback.invoke(api)
    }
}

data class DebuffIntent(
    override val enemy: Enemy,
    val debuffs: List<Status>,
    val inflictTileStatuses: List<TileStatusInflictStrategy>,
    val attackIntent: AttackIntent?,
    val callback: suspend (CombatApi) -> Unit = {},
    override val animation: IntentAnimation? = null,
) : Intent {

    override val displayData = if (attackIntent == null) {
        Pair(IntentDisplayData.DebuffIntentDisplay(), null)
    } else {
        Pair(attackIntent.displayData.first, IntentDisplayData.DebuffIntentDisplay())
    }

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        debuffs.forEach { api.addStatusToHero(it) }
        inflictTileStatuses.forEach { strategy ->
            api.inflictTileStatusOnHand(strategy)
        }
        callback.invoke(api)
    }
}

data class StrategicIntent(
    override val enemy: Enemy,
    val buffs: List<Status>,
    val debuffs: List<Status>,
    val inflictTileStatuses: List<TileStatusInflictStrategy>,
    val callback: suspend (CombatApi) -> Unit = {},
    override val animation: IntentAnimation? = null,
) : Intent {

    override val displayData = Pair(IntentDisplayData.BuffIntentDisplay(), IntentDisplayData.DebuffIntentDisplay())

    override suspend fun execute(api: CombatApi) {
        buffs.forEach { api.addStatusToEnemy(enemy, it) }
        debuffs.forEach { api.addStatusToHero(it) }
        inflictTileStatuses.forEach { strategy ->
            api.inflictTileStatusOnHand(strategy)
        }
        callback.invoke(api)
    }
}

data class FumbleIntent(
    override val enemy: Enemy, val amount: Int, val intent: Intent?, override val animation: IntentAnimation? = null,
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
        api.addTilesToHand(tiles, TileStatus.NONE, enemy)
    }
}

data class VentIntent(
    override val enemy: Enemy, val amount: Int, val status: Status?, override val animation: IntentAnimation? = null,
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

    fun predicate(tile: TileInstance, hand: List<TileInstance>): Boolean
}

data class RandomTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int,
) : TileStatusInflictStrategy {

    override fun predicate(tile: TileInstance, hand: List<TileInstance>): Boolean {
        return tile.tileStatus == TileStatus.NONE
    }
}

data class NonorphanedTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int,
) : TileStatusInflictStrategy {

    override fun predicate(tile: TileInstance, hand: List<TileInstance>): Boolean {
        return tile.tileStatus == TileStatus.NONE
                && tile.tile !is Tile.FumbleTile
                && !orphan(tile.tile, hand.map { it.tile })
    }
}

data class OrphanedTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int,
) : TileStatusInflictStrategy {

    override fun predicate(tile: TileInstance, hand: List<TileInstance>): Boolean {
        return tile.tileStatus == TileStatus.NONE
                && tile.tile !is Tile.FumbleTile
                && orphan(tile.tile, hand.map { it.tile })
    }
}
