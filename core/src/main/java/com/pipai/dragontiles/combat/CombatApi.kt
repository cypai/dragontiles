package com.pipai.dragontiles.combat

import com.pipai.dragontiles.data.Element
import com.pipai.dragontiles.data.Tile
import com.pipai.dragontiles.enemies.Enemy

class CombatApi(private val combat: Combat) {

    fun draw() {
        val tile = combat.drawPile.removeAt(0)
        if (combat.hand.size >= combat.hero.handSize) {
            combat.discardPile.add(tile)
        } else {
            combat.hand.add(tile)
        }
    }

    fun findTargets(): List<Enemy> {
        return combat.enemies
    }

    fun attack(target: Enemy, element: Element, amount: Int) {
        target.hp -= amount
    }

    fun consume(components: List<Tile>) {
        combat.discardPile.addAll(components)
    }

}
