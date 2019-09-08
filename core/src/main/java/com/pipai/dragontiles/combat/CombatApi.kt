package com.pipai.dragontiles.combat

import com.artemis.World
import com.pipai.dragontiles.artemis.systems.animation.*
import com.pipai.dragontiles.artemis.systems.combat.CombatAnimationSystem
import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy
import com.pipai.dragontiles.spells.SpellInstance

class CombatApi(val combat: Combat,
                val spellInstances: List<SpellInstance>,
                private val world: World) {

    private val animationSystem = world.getSystem(CombatAnimationSystem::class.java)

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
        combat.hand.sortWith(compareBy({ it.suit.order }, { it.order() }))
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
        combat.openPool.sortWith(compareBy({ it.suit.order }, { it.order() }))
        animationSystem.queueAnimation(AdjustOpenPoolAnimation(world, combat.openPool))
    }

    fun attack(target: Enemy, element: Element, amount: Int) {
        target.hp -= amount
        animationSystem.queueAnimation(DamageAnimation(world, target, amount))
    }

    fun consume(components: List<Tile>) {
        combat.hand.removeAll(components)
        combat.discardPile.addAll(components)
        val batchAnimation = BatchAnimation(world)
        components.map { ConsumeTileAnimation(world, it) }
                .forEach { batchAnimation.addToBatch(it) }
        animationSystem.queueAnimation(batchAnimation)
        sortHand()
    }

}
