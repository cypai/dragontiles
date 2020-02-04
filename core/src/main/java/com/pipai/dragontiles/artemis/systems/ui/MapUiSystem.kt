package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.ClickableComponent
import com.pipai.dragontiles.artemis.components.MapNodeComponent
import com.pipai.dragontiles.artemis.components.SpriteComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.events.MapNodeClickEvent
import com.pipai.dragontiles.artemis.screens.CombatScreen
import com.pipai.dragontiles.dungeon.MapNodeType
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import net.mostlyoriginal.api.event.common.Subscribe

class MapUiSystem(private val game: DragonTilesGame,
                  private val runData: RunData) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mMapNode by mapper<MapNodeComponent>()

    var canAdvanceMap = false

    override fun initialize() {
    }

    fun showMap() {
        val centerY = game.gameConfig.resolution.height.toFloat() / 2f
        val rightX = game.gameConfig.resolution.width.toFloat() - 64f
        val map = runData.dungeon.getMap()
        map.forEachIndexed { floorNum, floor ->
            val bottomY = centerY - floor.size * 64f / 2f
            floor.forEachIndexed { index, node ->
                val id = world.create()
                mXy.create(id).setXy(rightX - 64f * floorNum, bottomY + 64f * index)
                mSprite.create(id).sprite = when (node.type) {
                    MapNodeType.COMBAT -> {
                        Sprite(game.assets.get("assets/binassets/graphics/textures/fire_circle.png", Texture::class.java))
                    }
                    else -> {
                        Sprite(game.assets.get("assets/binassets/graphics/textures/any_circle.png", Texture::class.java))
                    }
                }
                mClickable.create(id).eventGenerator = { MapNodeClickEvent(floorNum, index) }
                mMapNode.create(id)
            }
        }
    }

    fun hideMap() {
        world.fetch(allOf(MapNodeComponent::class)).forEach { world.delete(it) }
    }

    @Subscribe
    fun handleMapNodeClick(ev: MapNodeClickEvent) {
        val map = runData.dungeon.getMap()
        if (canAdvanceMap
                && runData.dungeon.currentFloor == ev.floorNum - 1
                && map[runData.dungeon.currentFloor][runData.dungeon.currentFloorIndex].next.contains(ev.index)) {
            runData.dungeon.currentFloor = ev.floorNum
            runData.dungeon.currentFloorIndex = ev.index
            when (map[ev.floorNum][ev.index].type) {
                MapNodeType.COMBAT -> {
                    game.screen = CombatScreen(game, runData, runData.dungeon.easyEncounter(runData))
                }
                else -> {
                    game.screen = CombatScreen(game, runData, runData.dungeon.easyEncounter(runData))
                }
            }
        }
    }

    override fun processSystem() {
    }

}
