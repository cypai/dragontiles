package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.components.XYComponent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.PathInterpolationSystem
import com.pipai.dragontiles.data.*
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.utils.*
import net.mostlyoriginal.api.event.common.EventSystem

class RewardsSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
    private val startedBattleWon: Boolean,
) : NoProcessingSystem(), InputProcessor {

    private val skin = game.skin
    private val rootTable = Table()

    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()
    private val sMap by system<MapUiSystem>()
    private val sPath by system<PathInterpolationSystem>()

    private lateinit var api: GlobalApi

    private var active = false
    private var showing = false
    private var isOnSpellDraft = false

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        rootTable.setFillParent(true)
        stage.addActor(rootTable)
    }

    fun activateRewards() {
        if (!startedBattleWon) {
            game.writeSave()
        }
        world.fetch(allOf(XYComponent::class, TileComponent::class)).forEach {
            sPath.moveToLocation(it, game.gameConfig.resolution.width / 2f, -100f)
        }
        active = true
        showing = true
        buildAndShowRewardsTable()
    }

    private fun buildAndShowSpellDraftTable(spellDraftReward: Reward.SpellDraftReward) {
        isOnSpellDraft = true
        rootTable.clearChildren()

        val spellDraftTable = Table()
        rootTable.add(spellDraftTable)

        spellDraftReward.spells.forEach { spellInstance ->
            val spell = game.data.getSpell(spellInstance.id)
            val spellCard = SpellCard(game, spell, null, skin, null)
            spellCard.touchable = Touchable.enabled
            spellCard.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    api.addSpellToDeck(spell)
                    runData.combatRewards.remove(spellDraftReward)
                    buildAndShowRewardsTable()
                }

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addSpell(spell)
                    val screenXy = spellCard.localToScreenCoordinates(Vector2(0f, 0f))
                    sTooltip.showTooltip(
                        screenXy.x + SpellCard.cardWidth + 16,
                        game.gameConfig.resolution.height - screenXy.y
                    )
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            spellDraftTable.add(spellCard)
        }
        spellDraftTable.row()

        val skipButton = TextButton(" Skip (+1 Gold) ", skin)
        skipButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                runData.combatRewards.remove(spellDraftReward)
                api.gainGoldImmediate(1)
                buildAndShowRewardsTable()
            }
        })
        spellDraftTable.add(skipButton)
            .colspan(spellDraftReward.spells.size)
        spellDraftTable.row()

        val backButton = TextButton(" Back ", skin)
        backButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                buildAndShowRewardsTable()
            }
        })
        spellDraftTable.add(backButton)
            .colspan(spellDraftReward.spells.size)
    }

    private fun buildAndShowRewardsTable() {
        isOnSpellDraft = false
        rootTable.clearChildren()

        val rewardsTable = Table()
        rewardsTable.background = game.skin.getDrawable("frameDrawable")
        rootTable.add(rewardsTable)

        rewardsTable.add(Label("  Rewards!  ", skin))
            .pad(8f)
            .prefWidth(320f)
        rewardsTable.row()

        runData.combatRewards.forEach { reward ->
            val itemTable = when (reward) {
                is Reward.SpellDraftReward -> {
                    rewardItemTable({ buildAndShowSpellDraftTable(reward) }, Image(), null, "Spell Draft")
                }
                is Reward.GoldReward -> {
                    rewardItemTable({ getGold(reward.amount) }, Image(), null, "${reward.amount} Gold")
                }
                is Reward.RelicReward -> {
                    val relic = game.data.getRelic(reward.relic.id)
                    rewardItemTable(
                        { getRelic(reward.relic) },
                        Image(game.assets.get(relicAssetPath(relic.assetName), Texture::class.java)),
                        game.gameStrings.nameDescLocalization(reward.relic.id),
                        game.gameStrings.nameDescLocalization(reward.relic.id).name
                    )
                }
                is Reward.PotionReward -> {
                    val potion = game.data.getPotion(reward.potion)
                    rewardItemTable(
                        { getPotion(reward) },
                        Image(game.assets.get(potionAssetPath(potion.assetName), Texture::class.java)),
                        game.gameStrings.nameDescLocalization(reward.potion),
                        game.gameStrings.nameDescLocalization(reward.potion).name
                    )
                }
                is Reward.EmptyReward -> {
                    Table()
                }
            }
            rewardsTable.add(itemTable)
                .pad(4f)
                .height(64f)
                .width(320f)
                .left()
                .expand()
            rewardsTable.row()
        }
        val openMapBtn = TextButton("  Open Map  ", skin)
        openMapBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                sMap.showMap()
                handleOtherUiActivation()
            }
        })
        rewardsTable.add(openMapBtn)
            .pad(32f)
        rewardsTable.row()
    }

    private fun getGold(amount: Int) {
        runData.combatRewards.removeAll { it is Reward.GoldReward }
        runData.combatRewards.add(Reward.EmptyReward())
        api.gainGoldImmediate(amount)
        buildAndShowRewardsTable()
    }

    private fun getPotion(potionReward: Reward.PotionReward) {
        if (runData.hero.potionSlots.any { it.potionId == null }) {
            runData.combatRewards.remove(potionReward)
            runData.combatRewards.add(Reward.EmptyReward())
            api.gainPotion(game.data.getPotion(potionReward.potion))
            buildAndShowRewardsTable()
        }
    }

    private fun getRelic(relic: RelicInstance) {
        runData.combatRewards.removeAll { it is Reward.RelicReward && it.relic == relic }
        runData.combatRewards.add(Reward.EmptyReward())
        api.gainRelicImmediate(game.data.getRelic(relic.id))
        buildAndShowRewardsTable()
    }

    private fun rewardItemTable(
        onClick: () -> Unit,
        image: Image,
        localization: NameDescLocalization?,
        text: String
    ): Table {
        val table = Table()
        table.background = game.skin.getDrawable("frameDrawable")
        table.add(image)
            .prefWidth(64f)
            .prefHeight(64f)
            .left()
        table.add(Label(text, skin))
            .pad(8f)
            .left()
            .expand()
        table.touchable = Touchable.enabled
        table.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick.invoke()
            }

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                table.background = game.skin.getDrawable("frameDrawableLight")
                if (localization != null) {
                    sTooltip.addNameDescLocalization(localization)
                    sTooltip.showTooltip()
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                table.background = game.skin.getDrawable("frameDrawable")
                if (localization != null) {
                    sTooltip.hideTooltip()
                }
            }
        })
        return table
    }

    private fun handleOtherUiActivation() {
        if (showing) {
            showing = false
            rootTable.remove()
        } else {
            showing = true
            stage.addActor(rootTable)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                if (active && !showing && !isOnSpellDraft) {
                    handleOtherUiActivation()
                    sMap.hideMap()
                }
                if (isOnSpellDraft) {
                    buildAndShowRewardsTable()
                    return true
                }
            }
            Input.Keys.D -> {
                if (active) {
                    if (isOnSpellDraft) {
                        return true
                    } else {
                        handleOtherUiActivation()
                    }
                }
            }
            Input.Keys.M -> {
                if (active) {
                    handleOtherUiActivation()
                }
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
