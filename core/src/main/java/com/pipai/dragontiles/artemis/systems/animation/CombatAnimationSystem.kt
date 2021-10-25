package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.BaseSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.Subscribe

class CombatAnimationSystem(private val game: DragonTilesGame) : BaseSystem(), AnimationObserver {

    private val logger = getLogger()

    var pauseUiMode = false
    private var animating = false
    private val animationQueue: MutableList<Animation> = mutableListOf()
    private var turnRunning = false

    private val sUi by system<CombatUiSystem>()
    private val sCombat by system<CombatControllerSystem>()

    override fun processSystem() {
        if (!animating && animationQueue.isNotEmpty()) {
            animating = true
            if (pauseUiMode) {
                sUi.disable()
            }
            val animation = animationQueue.first()
            logger.debug("Start animation: $animation")
            animation.startAnimation()
        }
    }

    private fun queueAnimation(animation: Animation) {
        logger.debug("Received animation queue: $animation")
        animation.init(world, game)
        animation.initObserver(this)
        animationQueue.add(animation)
    }

    override fun notify(animation: Animation) {
        animationQueue.remove(animation)
        animating = false
        if (animationQueue.isEmpty() && turnRunning && pauseUiMode) {
            sUi.enable()
        }
    }

    @Subscribe
    fun handleTurnStartEvent(ev: TurnStartEvent) {
        turnRunning = true
    }

    @Subscribe
    fun handleTurnEndEvent(ev: TurnEndEvent) {
        turnRunning = false
    }

    @Subscribe
    fun handleDrawEvent(ev: DrawEvent) {
        val batch = BatchAnimation()
        ev.tiles.forEach {
            batch.addToBatch(DrawTileAnimation(it.first, it.second, sUi.layout))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleDrawFromOpenPoolEvent(ev: DrawFromOpenPoolEvent) {
        val batch = BatchAnimation()
        batch.addToBatch(AdjustHandAnimation(ev.tiles, sCombat.combat.assigned, sUi.layout))
        batch.addToBatch(AdjustOpenPoolAnimation(sCombat.combat.openPool, sUi.layout))
        queueAnimation(batch)
    }

    @Subscribe
    fun handleHandAdjustedEvent(ev: HandAdjustedEvent) {
        adjustHand()
    }

    private fun adjustHand() {
        queueAnimation(AdjustHandAnimation(sCombat.combat.hand.mapIndexed { index, tileInstance ->
            Pair(
                tileInstance,
                index
            )
        }, sCombat.combat.assigned, sUi.layout))
    }

    @Subscribe
    fun handleOpenDrawEvent(ev: DrawToOpenPoolEvent) {
        val batch = BatchAnimation()
        ev.tiles.forEach {
            batch.addToBatch(DrawToOpenPoolAnimation(it.first, it.second, sUi.layout))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleOpenPooltoDiscard(ev: OpenPoolToDiscardEvent) {
        queueAnimation(TileDestroyAnimation(ev.tiles))
    }

    @Subscribe
    fun handleOpenPoolAdjustedEvent(ev: OpenPoolAdjustedEvent) {
        queueAnimation(AdjustOpenPoolAnimation(ev.openPool, sUi.layout))
    }

    @Subscribe
    fun handleOpenDiscard(ev: OpenDiscardEvent) {
        queueAnimation(AdjustOpenPoolAnimation(sCombat.combat.openPool, sUi.layout))
    }

    @Subscribe
    fun handleStatusAdjusted(ev: StatusAdjustedEvent) {
        queueAnimation(StatusAdjustedAnimation(ev))
    }

    @Subscribe
    fun handleEnemyChangeIntent(ev: EnemyChangeIntentEvent) {
        queueAnimation(EnemyIntentAnimation(ev.enemy, ev.intent))
    }

    @Subscribe
    fun handleEnemyFluxDamageEvent(ev: EnemyFluxDamageEvent) {
        queueAnimation(EnemyFluxDamageAnimation(ev.enemy, ev.amount))
    }

    @Subscribe
    fun handleEnemyLoseFluxEvent(ev: EnemyLoseFluxEvent) {
        queueAnimation(EnemyLoseFluxAnimation(ev.enemy, ev.amount))
    }

    @Subscribe
    fun handleEnemyDamageEvent(ev: EnemyDamageEvent) {
        queueAnimation(EnemyDamageAnimation(ev.enemy, ev.amount))
    }

    @Subscribe
    fun handlePlayerFluxDamageEvent(ev: PlayerFluxDamageEvent) {
        queueAnimation(PlayerFluxDamageAnimation(ev.amount))
    }

    @Subscribe
    fun handlePlayerLoseFluxEvent(ev: PlayerLoseFluxEvent) {
        queueAnimation(PlayerLoseFluxAnimation(ev.amount))
    }

    @Subscribe
    fun handlePlayerDamageEvent(ev: PlayerDamageEvent) {
        queueAnimation(PlayerDamageAnimation(ev.amount))
    }

    @Subscribe
    fun handleComponentConsumeEvent(ev: ComponentConsumeEvent) {
        val batch = BatchAnimation()
        ev.components.forEach {
            batch.addToBatch(ConsumeTileAnimation(it))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleTransformation(ev: TileTransformedEvent) {
        queueAnimation(TileTransformAnimation(ev, game.tileSkin))
    }

    @Subscribe
    fun handleDestroy(ev: TileDestroyedEvent) {
        queueAnimation(TileDestroyAnimation(listOf(ev.tile)))
    }

    @Subscribe
    fun handleAddedToHand(ev: TilesAddedToHandEvent) {
        queueAnimation(TilesAddedToHandAnimation(ev.tiles, sUi.layout))
    }

    @Subscribe
    fun handleSelectQuery(ev: QueryTilesEvent) {
        queueAnimation(QueryTilesAnimation(ev))
    }

    @Subscribe
    fun handleOptionQuery(ev: QueryTileOptionsEvent) {
        queueAnimation(QueryTileOptionsAnimation(ev))
    }

    @Subscribe
    fun handleSwapQuery(ev: QuerySwapEvent) {
        queueAnimation(QuerySwapAnimation(ev))
    }

    @Subscribe
    fun handleSwap(ev: SwapEvent) {
        queueAnimation(SwapAnimation(ev.spellInHand, ev.spellOnSide))
    }

    @Subscribe
    fun handleEnemyDefeat(ev: EnemyDefeatedEvent) {
        queueAnimation(EnemyDefeatAnimation(ev.enemy))
    }

    @Subscribe
    fun handleGameOver(ev: GameOverEvent) {
        queueAnimation(GameOverAnimation())
    }

    @Subscribe
    fun handleBattleWin(ev: BattleWinEvent) {
        queueAnimation(BattleWinAnimation())
    }

    @Subscribe
    fun handleRuneActivation(ev: RuneActivatedEvent) {
        adjustHand()
    }

    @Subscribe
    fun handleRuneDeactivation(ev: RuneDeactivatedEvent) {
        adjustHand()
    }

}
