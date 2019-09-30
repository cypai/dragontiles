package com.pipai.dragontiles.combat

import com.artemis.World
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.animation.*
import com.pipai.dragontiles.artemis.systems.combat.CombatAnimationSystem
import com.pipai.dragontiles.data.CountdownAttack
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.TileInstance
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.SpellInstance

class CombatApi(val combat: Combat,
                val spellInstances: List<SpellInstance>,
                private val world: World,
                private val game: DragonTilesGame) {

    private val animationSystem = world.getSystem(CombatAnimationSystem::class.java)

    private var nextId = 0

    fun nextId(): Int {
        nextId += 1
        return nextId
    }

    fun draw(amount: Int) {
        val batchAnimation = BatchAnimation(world)

        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            if (combat.hand.size >= combat.hero.handSize) {
                combat.discardPile.add(tile)
            } else {
                combat.hand.add(tile)
                batchAnimation.addToBatch(DrawTileAnimation(world, tile, combat.hand.size))
            }
        }
        animationSystem.queueAnimation(batchAnimation)
    }

    fun sortHand() {
        combat.hand.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        animationSystem.queueAnimation(AdjustHandAnimation(world, combat.hand))
    }

    fun drawToOpenPool(amount: Int) {
        val batchAnimation = BatchAnimation(world)

        repeat(amount) {
            val tile = combat.drawPile.removeAt(0)
            combat.openPool.add(tile)
            batchAnimation.addToBatch(DrawToOpenPoolAnimation(world, tile, combat.openPool.size))
        }
        animationSystem.queueAnimation(batchAnimation)
    }

    fun sortOpenPool() {
        combat.openPool.sortWith(compareBy({ it.tile.suit.order }, { it.tile.order() }))
        animationSystem.queueAnimation(AdjustOpenPoolAnimation(world, combat.openPool))
    }

    fun attack(target: Enemy, element: Element, amount: Int) {
        var damage = amount + (combat.heroStatus[Status.POWER] ?: 0)
        val broken = when (element) {
            Element.FIRE -> combat.enemyStatus[target.id]!![Status.FIRE_BREAK] != null
            Element.ICE -> combat.enemyStatus[target.id]!![Status.ICE_BREAK] != null
            Element.LIGHTNING -> combat.enemyStatus[target.id]!![Status.LIGHTNING_BREAK] != null
            else -> false
        }
        if (broken) {
            damage *= 2
        }
        target.hp -= damage
        animationSystem.queueAnimation(DamageAnimation(world, target, damage))
    }

    fun consume(components: List<TileInstance>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        val batchAnimation = BatchAnimation(world)
        components.map { ConsumeTileAnimation(world, it) }
                .forEach { batchAnimation.addToBatch(it) }
        animationSystem.queueAnimation(batchAnimation)
        sortHand()
    }

    fun dealDamageToHero(damage: Int) {
        combat.hero.hp -= damage
        animationSystem.queueAnimation(PlayerDamageAnimation(world, damage, combat.hero.hp, combat.hero.hpMax))
        if (combat.hero.hp <= 0) {
            animationSystem.queueAnimation(GameOverAnimation(world))
        }
    }

    fun enemyAttack(enemy: Enemy, countdownAttack: CountdownAttack) {
        combat.incomingAttacks.add(countdownAttack)
        animationSystem.queueAnimation(CreateAttackCircleAnimation(world, game, enemy, countdownAttack))
    }

    fun updateCountdownAttack(countdownAttack: CountdownAttack) {
        countdownAttack.turnsLeft -= 1
        if (countdownAttack.turnsLeft == 0) {
            combat.incomingAttacks.remove(countdownAttack)
            animationSystem.queueAnimation(ResolveAttackCircleAnimation(world, countdownAttack))
            val damage = countdownAttack.baseDamage * countdownAttack.multiplier
            dealDamageToHero(damage)
        } else {
            animationSystem.queueAnimation(UpdateAttackCircleAnimation(world, countdownAttack))
        }
    }

    fun changeStatus(status: Status, amount: Int) {
        combat.heroStatus[status] = amount
    }

    fun changeStatusIncrement(status: Status, increment: Int) {
        changeStatus(status, (combat.heroStatus[status] ?: 0) + increment)
    }

    fun changeEnemyStatus(id: Int, status: Status, amount: Int) {
        combat.enemyStatus[id]!![status] = amount
    }

    fun changeEnemyStatusIncrement(id: Int, status: Status, increment: Int) {
        changeEnemyStatus(id, status, (combat.enemyStatus[id]!![status] ?: 0) + increment)
    }

}
