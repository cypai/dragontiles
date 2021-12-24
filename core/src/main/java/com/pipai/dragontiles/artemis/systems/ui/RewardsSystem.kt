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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.pipai.dragontiles.DragonTilesGame
import com.pipai.dragontiles.artemis.components.TileComponent
import com.pipai.dragontiles.artemis.systems.NoProcessingSystem
import com.pipai.dragontiles.combat.CombatRewards
import com.pipai.dragontiles.data.GameData
import com.pipai.dragontiles.data.NameDescLocalization
import com.pipai.dragontiles.dungeon.GlobalApi
import com.pipai.dragontiles.dungeon.RunData
import com.pipai.dragontiles.gui.SpellCard
import com.pipai.dragontiles.potions.Potion
import com.pipai.dragontiles.relics.Relic
import com.pipai.dragontiles.relics.RelicInstance
import com.pipai.dragontiles.spells.PostExhaustAspect
import com.pipai.dragontiles.spells.Spell
import com.pipai.dragontiles.spells.SpellInstance
import com.pipai.dragontiles.utils.*
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
    private val sMap by system<MapUiSystem>()

    private lateinit var api: GlobalApi

    private var active = false
    private var showing = false
    private var isOnSpellDraft = false

    override fun initialize() {
        api = GlobalApi(game.data, runData, sEvent)
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        val heroClass = game.data.getHeroClass(runData.hero.heroClassId)
        val rng = runData.seed.rewardRng()
        actualRewards.add(Reward.SpellDraftReward(heroClass.getRandomClassSpells(runData.seed, 3).map { it.toInstance() }))
        actualRewards.add(Reward.GoldReward(rewards.gold))
        if (rng.nextFloat() < rewards.potionChance) {
            actualRewards.add(Reward.PotionReward(game.data.allPotions().choose(rng).id))
            runData.potionChance = GameData.BASE_POTION_CHANCE
        } else {
            runData.potionChance += 0.1f
        }
        if (rewards.randomRelic) {
            actualRewards.add(Reward.RelicReward(RelicInstance(runData.availableRelics.choose(rng), 0)))
        }
        if (rewards.relic != null) {
            actualRewards.add(Reward.RelicReward(rewards.relic.toInstance()))
        }
    }

    fun activateRewards() {
        world.fetch(allOf(TileComponent::class)).forEach { world.delete(it) }
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
                    actualRewards.remove(spellDraftReward)
                    buildAndShowRewardsTable()
                }

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    sTooltip.addKeywordsInString(game.gameStrings.spellLocalization(spell.id).description)
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
                handleMapActivation()
            }
        })
        rewardsTable.add(openMapBtn)
            .pad(32f)
        rewardsTable.row()
    }

    private fun getGold() {
        actualRewards.removeAll { it is Reward.GoldReward }
        actualRewards.add(Reward.EmptyReward())
        api.gainGoldImmediate(rewards.gold)
        buildAndShowRewardsTable()
    }

    private fun getPotion(potionReward: Reward.PotionReward) {
        actualRewards.remove(potionReward)
        actualRewards.add(Reward.EmptyReward())
        api.gainPotion(game.data.getPotion(potionReward.potion))
        buildAndShowRewardsTable()
    }

    private fun getRelic(relic: RelicInstance) {
        actualRewards.removeAll { it is Reward.RelicReward && it.relic == relic }
        actualRewards.add(Reward.EmptyReward())
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

    sealed class Reward {
        data class SpellDraftReward(val spells: List<SpellInstance>) : Reward()
        data class GoldReward(val amount: Int) : Reward()
        data class RelicReward(val relic: RelicInstance) : Reward()
        data class PotionReward(val potion: String) : Reward()
        class EmptyReward : Reward()
    }

    private fun handleMapActivation() {
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
                    handleMapActivation()
                    sMap.hideMap()
                    return true
                }
                if (isOnSpellDraft) {
                    buildAndShowRewardsTable()
                    return true
                }
            }
            Input.Keys.M -> {
                if (active) {
                    handleMapActivation()
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
