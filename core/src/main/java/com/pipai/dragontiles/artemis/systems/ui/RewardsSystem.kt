package com.pipai.dragontiles.artemis.systems.ui

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.artemis.systems.combat.CombatControllerSystem
import com.pipai.dragontiles.artemis.systems.rendering.FullScreenColorSystem
import com.pipai.dragontiles.combat.CombatRewards
import com.pipai.dragontiles.data.NameDescLocalization
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.spells.PostExhaustAspect
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.utils.choose
import com.pipai.dragontiles.utils.relicAssetPath
import com.pipai.dragontiles.utils.system
import net.mostlyoriginal.api.event.common.EventSystem

class RewardsSystem(
    private val game: DragonTilesGame,
    private val runData: RunData,
    private val stage: Stage,
    private val rewards: CombatRewards,
) : NoProcessingSystem(), InputProcessor {

    private val skin = game.skin
    private val actualRewards: MutableList<Reward> = mutableListOf()
    private val rootTable = Table()

    private val sEvent by system<EventSystem>()
    private val sTooltip by system<TooltipSystem>()

    private lateinit var api: GlobalApi

    private var isOnSpellDraft = false

    override fun initialize() {
        api = GlobalApi(runData, sEvent)
        rootTable.setFillParent(true)
        stage.addActor(rootTable)
    }

    fun generateRewards() {
        actualRewards.add(Reward.SpellDraftReward(runData.hero.heroClass.getRandomClassSpells(runData, 3)))
        actualRewards.add(Reward.GoldReward(rewards.gold))
        if (rewards.randomRelic) {
            actualRewards.add(Reward.RelicReward(runData.relicData.availableRelics.choose(runData.rng)))
        }
        if (rewards.relic != null) {
            actualRewards.add(Reward.RelicReward(rewards.relic))
        }
        buildAndShowRewardsTable()
    }

    private fun buildAndShowSpellDraftTable(spellDraftReward: Reward.SpellDraftReward) {
        isOnSpellDraft = true
        rootTable.clearChildren()

        val spellDraftTable = Table()
        rootTable.add(spellDraftTable)

        spellDraftReward.spells.forEach { spell ->
            val spellCard = SpellCard(game, spell, null, skin, null)
            spellCard.touchable = Touchable.enabled
            spellCard.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    api.addSpellToDeck(spell)
                    actualRewards.remove(spellDraftReward)
                    buildAndShowRewardsTable()
                }

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addKeywordsInString(game.gameStrings.spellLocalization(spell.strId).description)
                    if (spell.aspects.any { a -> a is PostExhaustAspect }) {
                        sTooltip.addKeyword("@Exhaust")
                    }
                    sTooltip.showTooltip()
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    sTooltip.hideTooltip()
                }
            })
            spellDraftTable.add(spellCard)
        }
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

        actualRewards.forEach { reward ->
            val itemTable = when (reward) {
                is Reward.SpellDraftReward -> {
                    rewardItemTable({ buildAndShowSpellDraftTable(reward) }, Image(), null, "Spell Draft")
                }
                is Reward.GoldReward -> {
                    rewardItemTable(this::getGold, Image(), null, "${rewards.gold} Gold")
                }
                is Reward.RelicReward -> {
                    rewardItemTable(
                        { getRelic(reward.relic) },
                        Image(game.assets.get(relicAssetPath(reward.relic.assetName), Texture::class.java)),
                        game.gameStrings.nameDescLocalization(reward.relic.strId),
                        game.gameStrings.nameDescLocalization(reward.relic.strId).name
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
    }

    private fun getGold() {
        actualRewards.removeAll { it is Reward.GoldReward }
        actualRewards.add(Reward.EmptyReward())
        api.gainGoldImmediate(rewards.gold)
        buildAndShowRewardsTable()
    }

    private fun getRelic(relic: Relic) {
        actualRewards.removeAll { it is Reward.RelicReward && it.relic == relic }
        actualRewards.add(Reward.EmptyReward())
        api.gainRelicImmediate(relic)
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

    sealed class Reward {
        data class SpellDraftReward(val spells: List<Spell>) : Reward()
        data class GoldReward(val amount: Int) : Reward()
        data class RelicReward(val relic: Relic) : Reward()
        class EmptyReward : Reward()
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE -> {
                if (isOnSpellDraft) {
                    buildAndShowRewardsTable()
                    return true
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
