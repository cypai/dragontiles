package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.data.TileStatus
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.status.Status
import com.pipai.dragontiles.utils.chooseAmount
import kotlin.random.Random

interface Intent {
    val enemy: Enemy
    val type: IntentType
    suspend fun execute(api: CombatApi)
}

enum class IntentType {
    ATTACK, VENT, BUFF, DEBUFF, STUNNED
}

data class AttackIntent(
    override val enemy: Enemy, val attackPower: Int, val multistrike: Int, val piercing: Boolean, val element: Element
) : Intent {

    override val type: IntentType = IntentType.ATTACK

    override suspend fun execute(api: CombatApi) {
        repeat(multistrike) {
            api.attackHero(enemy, element, attackPower)
        }
    }
}

data class FumbleIntent(
    override val enemy: Enemy, val amount: Int, val intent: Intent?
) : Intent {

    override val type: IntentType = intent?.type ?: IntentType.DEBUFF

    override suspend fun execute(api: CombatApi) {
        val tiles: MutableList<Tile> = mutableListOf()
        repeat(amount) {
            tiles.add(Tile.FumbleTile())
        }
        api.addTilesToHand(tiles, TileStatus.NONE)
        intent?.execute(api)
    }
}

data class BuffIntent(
    override val enemy: Enemy, val status: Status, val attackIntent: AttackIntent?
) : Intent {

    override val type: IntentType = IntentType.BUFF

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

    override val type: IntentType = IntentType.DEBUFF

    override suspend fun execute(api: CombatApi) {
        attackIntent?.execute(api)
        status?.let { api.addStatusToHero(status) }
        inflictTileStatuses.forEach { strategy ->
            api.inflictTileStatusOnHand(strategy)
        }
    }
}

interface TileStatusInflictStrategy {
    val tileStatus: TileStatus
    val amount: Int
    fun select(hand: List<TileInstance>, rng: Random): List<TileInstance>
}

data class RandomTileStatusInflictStrategy(
    override val tileStatus: TileStatus,
    override val amount: Int
) : TileStatusInflictStrategy {

    override fun select(hand: List<TileInstance>, rng: Random): List<TileInstance> {
        return hand
            .filter { it.tileStatus == TileStatus.NONE }
            .chooseAmount(amount, rng)
    }
}

data class StunnedIntent(override val enemy: Enemy) : Intent {

    override val type: IntentType = IntentType.STUNNED

    override suspend fun execute(api: CombatApi) {
    }
}
