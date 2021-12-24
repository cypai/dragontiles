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
import com.pipai.dragontiles.combat.CombatRewardConfig
import com.pipai.dragontiles.combat.SpellRewardType
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.dungeon.MapNodeType
import com.pipai.dragontiles.utils.allOf
import com.pipai.dragontiles.utils.choose
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
        val map = runData.dungeonMap.map
        var previousFloorIds: List<Int> = listOf()
        map.forEachIndexed { floorNum, floor ->
            val bottomY = centerY - floor.size * 64f / 2f
            val cuurentFloorIds: MutableList<Int> = mutableListOf()
            floor.forEachIndexed { index, node ->
                val id = world.create()
                mXy.create(id).setXy(rightX - 64f * floorNum, bottomY + 64f * index)
                mSprite.create(id).sprite =
                    if (runData.dungeonMap.currentFloor == floorNum && runData.dungeonMap.currentFloorIndex == index) {
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
        val rng = runData.seed.miscRng()
        val map = runData.dungeonMap.map
        val dungeon = game.data.getDungeon(runData.dungeonMap.dungeonId)
        if (canAdvanceMap
            && runData.dungeonMap.currentFloor == ev.floorNum - 1
            && map[runData.dungeonMap.currentFloor][runData.dungeonMap.currentFloorIndex].next.contains(ev.index)
        ) {
            val node = runData.dungeonMap.changeNode(ev.floorNum, ev.index)
            when (node.type) {
                MapNodeType.COMBAT -> {
                    val encounter = if (runData.dungeonMap.easyFights < 2) {
                        runData.dungeonMap.easyFights++
                        dungeon.easyEncounters
                            .filter { it.id !in runData.dungeonMap.encounters }
                            .choose(rng)
                    } else {
                        dungeon.standardEncounters
                            .filter { it.id !in runData.dungeonMap.encounters }
                            .choose(rng)
                    }
                    runData.runHistory.history.add(
                        FloorHistory.CombatFloorHistory(
                            runData.dungeonMap.dungeonId,
                            ev.floorNum,
                            encounter.id,
                            null
                        )
                    )
                    runData.combatWon = false
                    game.screen = CombatScreen(
                        game,
                        runData,
                        encounter,
                        CombatRewardConfig.standard(runData),
                        true,
                    )
                }
                MapNodeType.ELITE -> {
                    val encounter = dungeon.eliteEncounters
                        .filter { it.id !in runData.dungeonMap.encounters }
                        .choose(rng)
                    runData.runHistory.history.add(
                        FloorHistory.EliteFloorHistory(
                            runData.dungeonMap.dungeonId,
                            ev.floorNum,
                            encounter.id,
                            null
                        )
                    )
                    runData.combatWon = false
                    game.screen = CombatScreen(
                        game,
                        runData,
                        encounter,
                        CombatRewardConfig.elite(runData),
                        true,
                    )
                }
                MapNodeType.EVENT -> {
                    val event = dungeon.dungeonEvents
                        .filter { it.id !in runData.dungeonMap.dungeonEvents }
                        .choose(rng)
                    runData.runHistory.history.add(
                        FloorHistory.EventFloorHistory(
                            runData.dungeonMap.dungeonId,
                            ev.floorNum,
                            event.id,
                            null
                        )
                    )
                    game.screen = EventScreen(game, runData, event)
                }
                MapNodeType.TOWN -> {
                    TownGenerator().generate(game.data, runData)
                    val town = runData.town!!
                    runData.runHistory.history.add(
                        FloorHistory.TownFloorHistory(
                            runData.dungeonMap.dungeonId,
                            ev.floorNum,
                            false,
                            false,
                            town.dungeonEventId,
                            SpellShop(
                                town.spellShop.classSpells.toMutableList(),
                                town.spellShop.sorceries.toMutableList(),
                                town.spellShop.colorlessSpell?.copy()
                            ),
                            ItemShop(
                                town.itemShop.relics.toMutableList()
                            ),
                            Scribe(
                                town.scribe.upgrades.toMutableList()
                            ),
                            null
                        )
                    )
                    game.screen = TownScreen(game, runData)
                }
                else -> {
                    throw NotImplementedError()
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
