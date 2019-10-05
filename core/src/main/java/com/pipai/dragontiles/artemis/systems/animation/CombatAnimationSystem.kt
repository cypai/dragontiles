package com.pipai.dragontiles.artemis.systems.animation

import com.artemis.BaseSystem
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.ui.CombatUiSystem
import com.pipai.dragontiles.combat.*
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.Subscribe

class CombatAnimationSystem(private val game: DragonTilesGame) : BaseSystem(), AnimationObserver {

    private var animating = false
    private val animationQueue: MutableList<Animation> = mutableListOf()

    private val sUi by system<CombatUiSystem>()

    override fun processSystem() {
        if (!animating && animationQueue.isNotEmpty()) {
            animating = true
            sUi.disable()
            animationQueue.first().startAnimation()
        }
    }

    private fun queueAnimation(animation: Animation) {
        animation.init(world, game)
        animation.initObserver(this)
        animationQueue.add(animation)
    }

    override fun notify(animation: Animation) {
        animationQueue.remove(animation)
        animating = false
        if (animationQueue.isEmpty()) {
            sUi.enable()
        }
    }

    @Subscribe
    fun handleDrawEvent(ev: DrawEvent) {
        val batch = BatchAnimation()
        ev.tiles.forEach {
            batch.addToBatch(DrawTileAnimation(it.first, it.second))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleHandAdjustedEvent(ev: HandAdjustedEvent) {
        queueAnimation(AdjustHandAnimation(ev.hand))
    }

    @Subscribe
    fun handleOpenDrawEvent(ev: DrawToOpenPoolEvent) {
        val batch = BatchAnimation()
        ev.tiles.forEach {
            batch.addToBatch(DrawToOpenPoolAnimation(it.first, it.second))
        }
        queueAnimation(batch)
    }

    @Subscribe
    fun handleOpenPoolAdjustedEvent(ev: OpenPoolAdjustedEvent) {
        queueAnimation(AdjustOpenPoolAnimation(ev.openPool))
    }

    @Subscribe
    fun handleEnemyDamageEvent(ev: EnemyDamageEvent) {
        queueAnimation(DamageAnimation(ev.target, ev.amount))
    }

    @Subscribe
    fun handlePlayerDamageEvent(ev: PlayerDamageEvent) {
        queueAnimation(PlayerDamageAnimation(ev.amount))
    }

    @Subscribe
    fun handleEnemyCountdownAttackEvent(ev: EnemyCountdownAttackEvent) {
        queueAnimation(CreateAttackCircleAnimation(ev.enemy, ev.countdownAttack))
    }

    @Subscribe
    fun handleCountdownAttackTickEvent(ev: CountdownAttackTickEvent) {
        queueAnimation(UpdateAttackCircleAnimation(ev.countdownAttack))
    }

    @Subscribe
    fun handleCountdownResolveEvent(ev: CountdownAttackResolveEvent) {
        queueAnimation(ResolveAttackCircleAnimation(ev.countdownAttack))
    }

    @Subscribe
    fun handleComponentConsumeEvent(ev: ComponentConsumeEvent) {
        val batch = BatchAnimation()
        ev.components.forEach {
            batch.addToBatch(ConsumeTileAnimation(it))
        }
        queueAnimation(batch)
    }
}
