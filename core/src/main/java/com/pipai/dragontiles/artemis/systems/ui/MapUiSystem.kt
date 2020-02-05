package com.pipai.dragontiles.artemis.systems.ui

import com.artemis.BaseSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.*
import com.pipai.dragontiles.artemis.events.MapNodeClickEvent
import com.pipai.dragontiles.artemis.screens.CombatScreen
import com.pipai.dragontiles.dungeon.MapNodeType
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import net.mostlyoriginal.api.event.common.Subscribe

class MapUiSystem(private val game: DragonTilesGame,
                  private val stage: Stage,
                  private val runData: RunData) : BaseSystem() {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mMapNode by mapper<MapNodeComponent>()
    private val mAnchoredLine by mapper<AnchoredLineComponent>()
    private val mMutualDestroy by mapper<MutualDestroyComponent>()

    var canAdvanceMap = false

    override fun initialize() {
    }

    fun showMap() {
        val table = Table()
        table.setFillParent(true)
        table.background = game.skin.getDrawable("frameDrawableDark")
        stage.addActor(table)
        val centerY = game.gameConfig.resolution.height.toFloat() / 2f
        val rightX = game.gameConfig.resolution.width.toFloat() - 64f
        val map = runData.dungeon.getMap()
        var previousFloorIds: List<Int> = listOf()
        map.forEachIndexed { floorNum, floor ->
            val bottomY = centerY - floor.size * 64f / 2f
            val cuurentFloorIds: MutableList<Int> = mutableListOf()
            floor.forEachIndexed { index, node ->
                val id = world.create()
                mXy.create(id).setXy(rightX - 64f * floorNum, bottomY + 64f * index)
                mSprite.create(id).sprite = if (runData.dungeon.currentFloor == floorNum && runData.dungeon.currentFloorIndex == index) {
                    Sprite(game.assets.get("assets/binassets/graphics/textures/lightning_circle.png", Texture::class.java))
                } else {
                    when (node.type) {
                        MapNodeType.COMBAT -> {
                            Sprite(game.assets.get("assets/binassets/graphics/textures/fire_circle.png", Texture::class.java))
                        }
                        MapNodeType.TOWN -> {
                            Sprite(game.assets.get("assets/binassets/graphics/textures/ice_circle.png", Texture::class.java))
                        }
                        MapNodeType.BOSS -> {
                            Sprite(game.assets.get("assets/binassets/graphics/textures/star_circle.png", Texture::class.java))
                        }
                        else -> {
                            Sprite(game.assets.get("assets/binassets/graphics/textures/any_circle.png", Texture::class.java))
                        }
                    }
                }
                mClickable.create(id).eventGenerator = { MapNodeClickEvent(floorNum, index) }
                mMapNode.create(id)
                cuurentFloorIds.add(id)
                node.prev.forEach { prevIndex ->
                    val lineId = world.create()
                    val cAnchoredLine = mAnchoredLine.create(lineId)
                    cAnchoredLine.safeSetAnchor1(lineId, previousFloorIds[prevIndex], mMutualDestroy)
                    cAnchoredLine.safeSetAnchor2(lineId, id, mMutualDestroy)
                    cAnchoredLine.anchor1Offset = Vector2(16f, 16f)
                    cAnchoredLine.anchor2Offset = Vector2(16f, 16f)
                    cAnchoredLine.color = Color.WHITE
                }
            }
            previousFloorIds = cuurentFloorIds
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
