package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
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
import com.pipai.dragontiles.artemis.screens.EventScreen
import com.pipai.dragontiles.artemis.screens.TownScreen
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.CombatRewards
import com.pipai.dragontiles.combat.SpellRewardType
import com.pipai.dragontiles.data.PricedSpell
import com.pipai.dragontiles.data.SpellShop
import com.pipai.dragontiles.data.Town
import com.pipai.dragontiles.dungeon.MapNodeType
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.dungeonevents.DragonInquiryEvent
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.fetch
import com.pipai.dragontiles.utils.mapper
import net.mostlyoriginal.api.event.common.Subscribe

class MapUiSystem(
    private val game: DragonTilesGame,
    private val stage: Stage,
    private val runData: RunData
) : NoProcessingSystem(), InputProcessor {

    private val mXy by mapper<XYComponent>()
    private val mSprite by mapper<SpriteComponent>()
    private val mClickable by mapper<ClickableComponent>()
    private val mMapNode by mapper<MapNodeComponent>()
    private val mAnchoredLine by mapper<AnchoredLineComponent>()
    private val mMutualDestroy by mapper<MutualDestroyComponent>()

    private val table = Table()

    var canAdvanceMap = false
    var showing = false
        private set

    fun showMap() {
        showing = true
        table.background = game.skin.getDrawable("frameDrawableDark")
        table.width = game.gameConfig.resolution.width.toFloat()
        table.height = game.gameConfig.resolution.height.toFloat() / 2f
        table.y = table.height / 2f
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
                mSprite.create(id).sprite =
                    if (runData.dungeon.currentFloor == floorNum && runData.dungeon.currentFloorIndex == index) {
                        Sprite(
                            game.assets.get(
                                "assets/binassets/graphics/textures/lightning_circle.png",
                                Texture::class.java
                            )
                        )
                    } else {
                        when (node.type) {
                            MapNodeType.COMBAT -> {
                                Sprite(
                                    game.assets.get(
                                        "assets/binassets/graphics/textures/fire_circle.png",
                                        Texture::class.java
                                    )
                                )
                            }
                            MapNodeType.ELITE -> {
                                Sprite(
                                    game.assets.get(
                                        "assets/binassets/graphics/textures/star_circle.png",
                                        Texture::class.java
                                    )
                                )
                            }
                            MapNodeType.TOWN -> {
                                Sprite(
                                    game.assets.get(
                                        "assets/binassets/graphics/textures/ice_circle.png",
                                        Texture::class.java
                                    )
                                )
                            }
                            MapNodeType.BOSS -> {
                                Sprite(
                                    game.assets.get(
                                        "assets/binassets/graphics/textures/star_circle.png",
                                        Texture::class.java
                                    )
                                )
                            }
                            else -> {
                                Sprite(
                                    game.assets.get(
                                        "assets/binassets/graphics/textures/any_circle.png",
                                        Texture::class.java
                                    )
                                )
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
        table.remove()
        showing = false
    }

    @Subscribe
    fun handleMapNodeClick(ev: MapNodeClickEvent) {
        val map = runData.dungeon.getMap()
        if (canAdvanceMap
            && runData.dungeon.currentFloor == ev.floorNum - 1
            && map[runData.dungeon.currentFloor][runData.dungeon.currentFloorIndex].next.contains(ev.index)
        ) {
            runData.dungeon.currentFloor = ev.floorNum
            runData.dungeon.currentFloorIndex = ev.index
            when (map[ev.floorNum][ev.index].type) {
                MapNodeType.COMBAT -> {
                    if (runData.dungeon.easyFights < 2) {
                        runData.dungeon.easyFights++
                        game.screen = CombatScreen(
                            game,
                            runData,
                            runData.dungeon.easyEncounter(runData),
                            CombatRewards(SpellRewardType.STANDARD, 3, false, null)
                        )
                    } else {
                        game.screen = CombatScreen(
                            game,
                            runData,
                            runData.dungeon.standardEncounter(runData),
                            CombatRewards(SpellRewardType.STANDARD, 3, false, null)
                        )
                    }
                }
                MapNodeType.ELITE -> {
                    game.screen = CombatScreen(
                        game,
                        runData,
                        runData.dungeon.eliteEncounter(runData),
                        CombatRewards(SpellRewardType.ELITE, 5, true, null)
                    )
                }
                MapNodeType.EVENT -> {
                    game.screen = EventScreen(game, runData, DragonInquiryEvent())
                }
                MapNodeType.TOWN -> {
                    game.screen = TownScreen(game, runData, true)
                }
                else -> {
                    game.screen = CombatScreen(
                        game,
                        runData,
                        runData.dungeon.standardEncounter(runData),
                        CombatRewards(SpellRewardType.STANDARD, 3, false, null)
                    )
                }
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.M) {
            if (showing) {
                hideMap()
            } else {
                showMap()
            }
        }
        return false
    }

    override fun keyUp(keycode: Int) = false

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amountX: Float, amountY: Float) = false
}
