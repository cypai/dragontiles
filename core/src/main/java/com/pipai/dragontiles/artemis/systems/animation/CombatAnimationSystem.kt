package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.BaseSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.events.TopRowUiUpdateEvent
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.status.Overloaded
import com.pipai.dragontiles.utils.getLogger
import com.pipai.dragontiles.utils.system
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import net.mostlyoriginal.api.event.common.EventSystem
import net.mostlyoriginal.api.event.common.Subscribe

class CombatAnimationSystem(private val game: DragonTilesGame) : BaseSystem(), AnimationObserver {

    private val logger = getLogger()
    private val scope = CoroutineScope(Dispatchers.Default)

    var pauseUiMode = false
    private var animating = false
    private val animationQueue: MutableList<Animation> = mutableListOf()
    private var turnRunning = false
    private lateinit var animationChannel: Channel<Animation>
    private var durationTimer = 0f

    private val sEvent by system<EventSystem>()
    private val sUi by system<CombatUiSystem>()
    private val sCombat by system<CombatControllerSystem>()

    override fun initialize() {
        animationChannel = sCombat.controller.api.animationChannel
    }

    override fun processSystem() {
        if (animating) {
            val animation = animationQueue.first()
            if (animation.duration >= 0f) {
                durationTimer += world.delta
                if (durationTimer > animation.duration) {
                    endAnimation(animation)
                } else {
                    animation.process()
                }
            }
        } else {
            if (animationQueue.isNotEmpty()) {
                animating = true
                durationTimer = 0f
                if (pauseUiMode) {
                    sUi.disable()
                }
                val animation = animationQueue.first()
                logger.debug("Start animation: $animation")
                animation.startAnimation()
            }
        }
    }

    fun queueAnimation(animation: Animation) {
        logger.debug("Received animation queue: $animation")
        animation.init(world, game)
        animation.initObserver(this)
        animationQueue.add(animation)
    }

    fun isEmpty(): Boolean = animationQueue.isEmpty()

    private fun endAnimation(animation: Animation) {
        animationQueue.remove(animation)
        animating = false
        if (animationQueue.isEmpty() && turnRunning && pauseUiMode) {
            sUi.enable()
        }
    }

    override fun notify(animation: Animation) {
        endAnimation(animation)
    }

    @Subscribe
    fun handleTurnStartEvent(@Suppress("UNUSED_PARAMETER") ev: TurnStartEvent) {
        turnRunning = true
    }

    @Subscribe
    fun handleTurnEndEvent(@Suppress("UNUSED_PARAMETER") ev: TurnEndEvent) {
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
    fun handleHandAdjustedEvent(@Suppress("UNUSED_PARAMETER") ev: HandAdjustedEvent) {
        adjustHand()
    }

    fun adjustHand() {
        val batch = BatchAnimation()
        batch.addToBatch(AdjustHandAnimation(sCombat.combat.hand.mapIndexed { index, tileInstance ->
            Pair(
                tileInstance,
                index
            )
        }, sCombat.combat.assigned, sUi.layout))
        batch.addToBatch(AdjustOpenPoolAnimation(sCombat.combat.openPool, sUi.layout))
        queueAnimation(batch)
    }

    @Subscribe
    fun handleOpenDrawEvent(ev: AddToOpenPoolEvent) {
        val batch = BatchAnimation()
        ev.tiles.forEach {
            batch.addToBatch(DrawToOpenPoolAnimation(it.first, it.second, sUi.layout))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleOpenPooltoDiscard(ev: OpenPoolToDiscardEvent) {
        queueAnimation(TileDiscardAnimation(ev.tiles, sUi.layout))
    }

    @Subscribe
    fun handleOpenPoolAdjustedEvent(ev: OpenPoolAdjustedEvent) {
        queueAnimation(AdjustOpenPoolAnimation(ev.openPool, sUi.layout))
    }

    @Subscribe
    fun handleOpenDiscard(@Suppress("UNUSED_PARAMETER") ev: OpenDiscardEvent) {
        queueAnimation(AdjustOpenPoolAnimation(sCombat.combat.openPool, sUi.layout))
    }

    @Subscribe
    fun handleTileDrawDiscard(ev: DrawDiscardedEvent) {
        queueAnimation(TileCreateImmediateDiscardAnimation(ev.tiles, null, sUi.layout))
    }

    @Subscribe
    fun handleTileAddedDiscard(ev: TilesAddedDiscardedEvent) {
        queueAnimation(TileCreateImmediateDiscardAnimation(ev.tiles, ev.originator, sUi.layout))
    }

    @Subscribe
    fun handleStatusAdjusted(ev: StatusOverviewAdjustedEvent) {
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
        queueAnimation(PlayerFluxDamageAnimation(ev))
    }

    @Subscribe
    fun handlePlayerLoseFluxEvent(ev: PlayerLoseFluxEvent) {
        queueAnimation(PlayerLoseFluxAnimation(ev.amount))
    }

    @Subscribe
    fun handlePlayerChangeTempMaxFluxEvent(ev: PlayerTempMaxFluxChangeEvent) {
        queueAnimation(PlayerChangeTempMaxFluxAnimation(ev.amount))
    }

    @Subscribe
    fun handlePlayerDamageEvent(ev: PlayerDamageEvent) {
        queueAnimation(PlayerDamageAnimation(ev.amount))
    }

    @Subscribe
    fun handlePlayerHealEvent(ev: PlayerHealEvent) {
        queueAnimation(PlayerHealAnimation(ev.amount))
    }

    @Subscribe
    fun handleEnemyHealEvent(ev: EnemyHealEvent) {
        queueAnimation(EnemyHealAnimation(ev.enemy, ev.amount))
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
        queueAnimation(TileTransformAnimation(ev, sUi.layout))
    }

    @Subscribe
    fun handleTileStatus(ev: TileStatusChangeEvent) {
        queueAnimation(TileStatusChangeAnimation(ev))
    }

    @Subscribe
    fun handleDestroy(ev: TileDestroyedEvent) {
        queueAnimation(TileDestroyAnimation(listOf(ev.tile)))
    }

    @Subscribe
    fun handleAddedToHand(ev: TilesAddedToHandEvent) {
        queueAnimation(TilesAddedToHandAnimation(ev.tiles, ev.originator, sUi.layout))
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
        queueAnimation(QuerySwapAnimation(ev.amount))
    }

    @Subscribe
    fun handleSwap(ev: SwapEvent) {
        queueAnimation(SwapAnimation(ev))
    }

    @Subscribe
    fun handleEnemyDefeat(ev: EnemyDefeatedEvent) {
        queueAnimation(EnemyDefeatAnimation(ev.enemy))
    }

    @Subscribe
    fun handlePotionUse(ev: PotionUseEvent) {
        sEvent.dispatch(TopRowUiUpdateEvent())
    }

    @Subscribe
    fun handleStatusChange(ev: PlayerStatusChangeEvent) {
        if (ev.status is Overloaded && ev.previousAmount == 0) {
            queueAnimation(OverloadedAnimation())
        }
    }

    @Subscribe
    fun handleEnemyStatusChange(ev: EnemyStatusChangeEvent) {
        if (ev.status is Overloaded && ev.previousAmount == 0) {
            queueAnimation(OverloadedAnimation())
        }
    }

    @Subscribe
    fun handleGameOver(@Suppress("UNUSED_PARAMETER") ev: GameOverEvent) {
        queueAnimation(GameOverAnimation())
    }

    @Subscribe
    fun handleBattleWin(@Suppress("UNUSED_PARAMETER") ev: BattleWinEvent) {
        queueAnimation(BattleWinAnimation())
    }

    @Subscribe
    fun handleRuneActivation(ev: RuneActivatedEvent) {
        queueAnimation(RuneChangeAnimation(ev.rune, true))
        adjustHand()
    }

    @Subscribe
    fun handleRuneDeactivation(ev: RuneDeactivatedEvent) {
        queueAnimation(RuneChangeAnimation(ev.rune, false))
        adjustHand()
    }

    @Subscribe
    fun handleEnemySummon(ev: EnemySummonEvent) {
        queueAnimation(EnemySummonAnimation(ev))
    }

    @Subscribe
    fun handleAnimation(ev: AnimationEvent) {
        queueAnimation(ev.animation)
    }

    override fun dispose() {
        scope.cancel()
    }
}
